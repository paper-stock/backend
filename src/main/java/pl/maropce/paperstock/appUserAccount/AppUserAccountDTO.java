package pl.maropce.paperstock.appUserAccount;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

public record AppUserAccountDTO(
        UUID id,
        UUID appUserId,
        String email,
        @JsonIgnore String password,
        Role role,
        boolean enabled
) {
}
