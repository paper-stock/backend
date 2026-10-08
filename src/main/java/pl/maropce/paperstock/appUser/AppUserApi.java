package pl.maropce.paperstock.appUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.maropce.paperstock.appUser.internal.AppUserService;
import pl.maropce.paperstock.appUser.internal.exception.UserNotFoundException;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppUserApi {

    private final AppUserService appUserService;

    public AppUserDTO create(CreateAppUserRequest createAppUserRequest) {
        return appUserService.create(createAppUserRequest);
    }

    public Optional<AppUserAuthenticationData> getUserAuthData(String email) {
        try {
            return Optional.of(appUserService.getAppUserAuthData(email));
        } catch (UserNotFoundException e) {
            log.warn("User not found for email: {}", email);
            return Optional.empty();
        }
    }

    public AppUserDTO findAppUserById(UUID appUserId) {
        return appUserService.findAppUserById(appUserId);
    }

    public void changePassword(UUID appUserId, String oldPassword, String newPassword) {
        appUserService.changePassword(appUserId, oldPassword, newPassword);
    }

}