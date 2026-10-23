package pl.maropce.paperstock.jwt.internal.refreshToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.maropce.paperstock.appUserDetails.AppUserDetailServiceImpl;
import pl.maropce.paperstock.appUserDetails.AppUserDetails;
import pl.maropce.paperstock.jwt.TokenResponse;
import pl.maropce.paperstock.jwt.internal.JwtUtil;
import pl.maropce.paperstock.jwt.internal.refreshToken.exception.InvalidRefreshTokenException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.refresh-token.expiration}")
    private Long expirationTime;

    private final UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();

    private final RefreshTokenRepository refreshTokenRepository;
    private final AppUserDetailServiceImpl appUserDetailService;
    private final JwtUtil jwtUtils;

    @Transactional
    public String generateRefreshToken(AppUserDetails appUserDetails) {

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .username(appUserDetails.getUsername())
                .expiryDate(Instant.now().plusMillis(expirationTime))
                .build();
        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);

        return savedToken.getToken();
    }

    @Transactional
    public TokenResponse refreshAccessToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (isTokenExpired(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        AppUserDetails appUserDetails = appUserDetailService.loadUserByUsername(refreshToken.getUsername());

        userDetailsChecker.check(appUserDetails);

        refreshTokenRepository.delete(refreshToken);

        String newJwt = jwtUtils.generateToken(appUserDetails);
        String newRefreshToken = generateRefreshToken(appUserDetails);

        return new TokenResponse(newJwt, newRefreshToken);
    }

    private boolean isTokenExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }

    public void delete(String token) {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByToken(token);
        if (refreshTokenOptional.isPresent()) {
            RefreshToken refreshToken = refreshTokenOptional.get();
            log.debug("Deleting refresh token {}", refreshToken.getToken());

            refreshTokenRepository.delete(refreshToken);
        } else {
            log.warn("Attempted to delete a non-existent refresh token: {}", token);
        }
    }

    public void deleteAllExpired() {
        int count = refreshTokenRepository.deleteAllByExpiryDateBefore(Instant.now());
        log.info("Deleted {} expired refresh tokens", count);
    }

    public void deleteAllByUsername(@NonNull String username) {
        refreshTokenRepository.deleteAllByUsername(username);
    }
}
