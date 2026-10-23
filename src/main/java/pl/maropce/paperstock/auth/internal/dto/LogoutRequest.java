package pl.maropce.paperstock.auth.internal.dto;

public record LogoutRequest(
        String refreshToken
) {
}
