package pl.maropce.paperstock.security.auth.jwt.refreshToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.maropce.paperstock.security.AppUserDetails;
import pl.maropce.paperstock.security.auth.AppUserDetailsServiceImpl;
import pl.maropce.paperstock.security.auth.dto.LoginResponse;
import pl.maropce.paperstock.security.auth.jwt.JwtUtil;
import pl.maropce.paperstock.security.auth.jwt.refreshToken.exception.InvalidRefreshTokenException;

import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.refresh-token.expiration}")
    private Long EXPIRATION_TIME;

    private final UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();

    private final RefreshTokenRepository refreshTokenRepository;
    private final AppUserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtils;

    @Transactional
    public String generateRefreshToken(AppUserDetails appUserDetails) {

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .username(appUserDetails.getUsername())
                .expiryDate(Instant.now().plusMillis(EXPIRATION_TIME))
                .build();
        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);

        return savedToken.getToken();
    }

    @Transactional
    public LoginResponse refreshAccessToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (isTokenExpired(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        AppUserDetails appUserDetails = userDetailsService.loadUserByUsername(refreshToken.getUsername());

        userDetailsChecker.check(appUserDetails);

        refreshTokenRepository.delete(refreshToken);

        String newJwt = jwtUtils.generateToken(appUserDetails);
        String newRefreshToken = generateRefreshToken(appUserDetails);

        return new LoginResponse(newJwt, newRefreshToken);
    }

    private boolean isTokenExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }
}
