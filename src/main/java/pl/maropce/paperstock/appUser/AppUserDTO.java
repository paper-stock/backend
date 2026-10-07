package pl.maropce.paperstock.appUser;

import java.util.UUID;

public record AppUserDTO(
        UUID id,
        String displayName
) {}
