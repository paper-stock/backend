package pl.maropce.paperstock.appUser;


import java.util.UUID;

public record AppUserCreatedEvent(
        UUID userId
) {
}
