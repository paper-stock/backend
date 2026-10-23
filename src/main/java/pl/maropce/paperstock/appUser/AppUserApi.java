package pl.maropce.paperstock.appUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.maropce.paperstock.appUser.internal.AddFundsRequest;
import pl.maropce.paperstock.appUser.internal.AppUserService;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppUserApi {

    private final AppUserService appUserService;

    public AppUserDTO create(String displayName) {
        return appUserService.create(new CreateAppUserRequest(displayName));
    }

    public AppUserDTO findAppUserById(UUID appUserId) {
        return appUserService.findAppUserById(appUserId);
    }

    public void addFunds(UUID appUserId, BigDecimal amount) {
        appUserService.addFunds(appUserId, new AddFundsRequest(amount));
    }

    public void payFunds(UUID appUserId, BigDecimal funds) {
        appUserService.payFunds(appUserId, funds);
    }
}