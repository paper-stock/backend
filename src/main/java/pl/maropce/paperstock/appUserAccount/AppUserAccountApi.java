package pl.maropce.paperstock.appUserAccount;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.maropce.paperstock.appUserAccount.internal.AppUserAccountService;
import pl.maropce.paperstock.appUserAccount.internal.dto.CreateNewAccountRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppUserAccountApi {

    private final AppUserAccountService appUserAccountService;

    public boolean existsByEmail(String email) {
        return appUserAccountService.existsByEmail(email);
    }

    public void createNewAccount(String email, String password, UUID appUserId) {
        appUserAccountService.createNewAccount(new CreateNewAccountRequest(email, password, appUserId));
    }

    public AppUserAccountDTO findByEmail(String email) {
        return appUserAccountService.findByEmail(email);
    }

    public void changePassword(UUID appUserAccountId, String oldPassword, String newPassword) {
        appUserAccountService.changePassword(appUserAccountId, oldPassword, newPassword);
    }
}
