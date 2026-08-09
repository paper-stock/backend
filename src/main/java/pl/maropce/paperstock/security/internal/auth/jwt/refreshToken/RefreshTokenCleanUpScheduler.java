package pl.maropce.paperstock.security.internal.auth.jwt.refreshToken;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RefreshTokenCleanUpScheduler {

    private final RefreshTokenService refreshTokenService;

    @Scheduled(
            cron = "${refresh-token.cleanup.cron}",
            zone = "${refresh-token.cleanup.timezone}"
    )
    @Transactional
    public void deleteExpiredTokens() {
        refreshTokenService.deleteAllExpired();
    }
}
