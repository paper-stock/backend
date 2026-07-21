package pl.maropce.paperstock.auth;

import pl.maropce.paperstock.appUser.AppUserDTO;

public record AppUserRegisteredEvent(AppUserDTO appUserDTO) {
}
