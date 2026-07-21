package pl.maropce.paperstock.auth.internal.jwt.refreshToken;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.maropce.paperstock.auth.AppUserDetails;
import pl.maropce.paperstock.auth.internal.AppUserDetailsServiceImpl;
import pl.maropce.paperstock.auth.internal.dto.LoginResponse;
import pl.maropce.paperstock.auth.internal.jwt.JwtUtil;
import pl.maropce.paperstock.auth.internal.jwt.refreshToken.exception.InvalidRefreshTokenException;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.refresh-token.expiration}")
    private Long EXPIRATION_TIME;

    private final RefreshTokenRepository refreshTokenRepository;
    private final AppUserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtils;

    @Transactional
    public String generateRefreshToken(String appUserEmail) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(appUserEmail);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .username(userDetails.getUsername())
                .expiryDate(Instant.now().plusMillis(EXPIRATION_TIME))
                .build();
        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        return savedToken.getToken();
    }

    @Transactional
    public LoginResponse refreshAccessToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(InvalidRefreshTokenException::new);

        if(isTokenExpired(refreshToken)) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidRefreshTokenException();
        }

        AppUserDetails appUserDetails = userDetailsService.loadUserByUsername(refreshToken.getUsername());

        refreshTokenRepository.delete(refreshToken);

        String newJwt = jwtUtils.generateToken(appUserDetails);
        String newRefreshToken = generateRefreshToken(appUserDetails.getUsername());

        return new LoginResponse(newJwt, newRefreshToken);
    }

    private boolean isTokenExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }
}
