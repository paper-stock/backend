package pl.maropce.paperstock.auth.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.maropce.paperstock.appUserAccount.AppUserAccountDTO;
import pl.maropce.paperstock.appUserAccount.Role;
import pl.maropce.paperstock.appUserAccount.internal.AppUserAccount;
import pl.maropce.paperstock.appUserAccount.internal.AppUserAccountRepository;
import pl.maropce.paperstock.auth.internal.dto.RegisterUserRequest;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class DataInitializer {

    private final AuthService authService;
    private final AppUserAccountRepository appUserDetailsRepository;

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

        RegisterUserRequest registerUserRequest = new RegisterUserRequest(email, password, displayName);
        AppUserAccountDTO dto = authService.register(registerUserRequest);

        Optional<AppUserAccount> appUserAccountOptional = appUserDetailsRepository.findById(dto.id());

        if (appUserAccountOptional.isPresent()) {
            AppUserAccount appUserDetails = appUserAccountOptional.get();
            appUserDetails.setRole(role);
            appUserDetailsRepository.save(appUserDetails);
        } else {
            throw new RuntimeException("User not found after registration: " + email);
        }
    }
}
