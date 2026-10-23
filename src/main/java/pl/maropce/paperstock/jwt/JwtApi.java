package pl.maropce.paperstock.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.maropce.paperstock.jwt.internal.JwtUtil;
import pl.maropce.paperstock.jwt.internal.refreshToken.RefreshTokenService;
import pl.maropce.paperstock.security.AppUserDetails;

@Component
@RequiredArgsConstructor
public class JwtApi {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public String generateToken(AppUserDetails appUserDetails) {
        return jwtUtil.generateToken(appUserDetails);
    }

    public String generateRefreshToken(AppUserDetails appUserDetails) {
        return refreshTokenService.generateRefreshToken(appUserDetails);
    }

    public void deleteRefreshToken(String refreshToken) {
        refreshTokenService.delete(refreshToken);
    }

    public void deleteAllRefreshTokensByUsername(String username) {
        refreshTokenService.deleteAllByUsername(username);
    }

    public TokenResponse refreshAccessToken(String refreshToken) {
        return refreshTokenService.refreshAccessToken(refreshToken);
    }
}
