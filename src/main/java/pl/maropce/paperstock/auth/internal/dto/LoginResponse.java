package pl.maropce.paperstock.auth.internal.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}
