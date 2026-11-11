package pl.maropce.paperstock.auth.internal.dto;

public record WhoAmIResponse(
        String email,
        String role,
        boolean enabled,
        String displayName
) {
}
