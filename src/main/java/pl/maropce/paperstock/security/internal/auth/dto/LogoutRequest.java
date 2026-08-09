package pl.maropce.paperstock.security.internal.auth.dto;

public record LogoutRequest(
        String refreshToken
) {
}
