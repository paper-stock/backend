package pl.maropce.paperstock.security.auth.dto;

public record LoginRequest(
        String email,
        String password
) {
}
