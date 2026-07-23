package pl.maropce.paperstock.security.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}
