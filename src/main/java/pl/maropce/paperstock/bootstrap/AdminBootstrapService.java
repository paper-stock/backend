package pl.maropce.paperstock.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.maropce.paperstock.appUser.AppUserApi;
import pl.maropce.paperstock.appUser.AppUserDTO;
import pl.maropce.paperstock.appUserAccount.AppUserAccountApi;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminBootstrapService {

    private final AdminBootstrapProperties adminProperties;
    private final AppUserApi appUserApi;
    private final AppUserAccountApi appUserAccountApi;

    /**
     * Bootstraps an admin user if no admin user exists in the system.
     * This method should be called during application startup.
     **/
    public void bootstrapAdminAccount() {

        if (adminProperties.email() == null || adminProperties.email().isEmpty()
                || adminProperties.initialPassword() == null || adminProperties.initialPassword().isEmpty()
        ) {
            log.warn("Admin user bootstrap skipped due to missing email or initial password.");
            return;
        }

        if (appUserAccountApi.existsByRoleAdmin()) {
            log.info("Admin user already exists, skipping bootstrap.");
            return;
        }

        AppUserDTO appUserDTO = appUserApi.create("Admin");

        appUserAccountApi.createAdminAccount(adminProperties.email(), adminProperties.initialPassword(), appUserDTO.id());

        log.warn("Admin user has been bootstrapped successfully with initial password." +
                " Reset the password as soon as possible.");
    }
}
