package pl.maropce.paperstock.auth.internal.dto;

public record LoginRequest(
        String email,
        String password
) {
}
