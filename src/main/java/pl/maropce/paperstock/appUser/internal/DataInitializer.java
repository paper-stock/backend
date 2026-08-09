package pl.maropce.paperstock.appUser.internal;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.maropce.paperstock.appUser.Role;


@Component
@RequiredArgsConstructor
class DataInitializer {

    private final AppUserAccountRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${default.users.initialize:false}")
    private boolean initialize;

    @PostConstruct
    public void initialize() {
        if (!initialize) {
            return;
        }
        addUser("Admin", "admin@gmail.com", "password", Role.ADMIN);
        addUser("User", "user@gmail.com", "password", Role.USER);
    }

    public void addUser(String displayName, String email, String password, Role role) {
        AppUser appUser = AppUser.builder()
                .displayName(displayName)
                .build();
        AppUserAccount appUserAccount = AppUserAccount.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .isEnabled(true)
                .appUser(appUser)
                .build();
        appUserRepository.save(appUserAccount);
    }
}
