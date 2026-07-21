package pl.maropce.paperstock.appUser;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AppUserAuthenticationData(
        UUID appUserId,
        String email,
        String password,
        Role role,
        boolean isEnabled
) {
}
