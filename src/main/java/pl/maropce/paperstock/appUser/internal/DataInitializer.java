package pl.maropce.paperstock.appUser.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.maropce.paperstock.appUser.AppUserDTO;
import pl.maropce.paperstock.appUser.CreateAppUserRequest;
import pl.maropce.paperstock.appUser.Role;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class DataInitializer {


    private final AppUserService appUserService;
    private final AppUserAccountRepository appUserAccountRepository;

    @Value("${default.users.initialize:false}")
    private boolean initialize;

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        if (!initialize) {
            return;
        }
        addUser("Admin", "admin@gmail.com", "password", Role.ADMIN);
        addUser("User", "user@gmail.com", "password", Role.USER);
    }

    public void addUser(String displayName, String email, String password, Role role) {
        CreateAppUserRequest createAppUserRequest = new CreateAppUserRequest(email, password, displayName);

        AppUserDTO appUserDTO = appUserService.create(createAppUserRequest);
        Optional<AppUserAccount> userAccountOptional = appUserAccountRepository.findByAppUser_Id(appUserDTO.id());

        if (userAccountOptional.isPresent()) {
            AppUserAccount appUserAccount = userAccountOptional.get();
            appUserAccount.setRole(role);
            appUserAccountRepository.save(appUserAccount);
        } else {
            throw new IllegalStateException("User not found after creation: " + email);
        }
    }
}
