package pl.maropce.paperstock.appUserAccount.internal.exception;

import org.springframework.http.HttpStatus;
import pl.maropce.paperstock.exception.BaseException;

import java.util.UUID;

public class AppUserAccountNotFoundException extends BaseException {
    public AppUserAccountNotFoundException(UUID appUserAccountId) {
        super(HttpStatus.NOT_FOUND, "App user account does not exist: " + appUserAccountId);
    }

    public AppUserAccountNotFoundException(String email) {
        super(HttpStatus.NOT_FOUND, "App user account does not exist: " + email);
    }
}
