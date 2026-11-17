package pl.maropce.paperstock.auth;

import java.util.UUID;

public record UserRegisteredEvent(
        UUID appUserId,
        String email
) {
}
