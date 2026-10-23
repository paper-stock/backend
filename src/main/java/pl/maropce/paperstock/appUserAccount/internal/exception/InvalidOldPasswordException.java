package pl.maropce.paperstock.appUserAccount.internal.exception;

import org.springframework.http.HttpStatus;
import pl.maropce.paperstock.exception.BaseException;

import java.util.UUID;

public class InvalidOldPasswordException extends BaseException {
    public InvalidOldPasswordException(UUID appUserId) {
        super(HttpStatus.BAD_REQUEST, String.format("Invalid old password for account: %s", appUserId));
    }
}
