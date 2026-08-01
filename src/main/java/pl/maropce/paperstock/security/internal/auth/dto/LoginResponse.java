package pl.maropce.paperstock.security.internal.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}
