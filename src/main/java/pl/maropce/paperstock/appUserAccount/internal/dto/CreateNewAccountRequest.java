package pl.maropce.paperstock.appUserAccount.internal.dto;

import java.util.UUID;

public record CreateNewAccountRequest(

        String email,
        String password,
        UUID appUserId
) {
}
