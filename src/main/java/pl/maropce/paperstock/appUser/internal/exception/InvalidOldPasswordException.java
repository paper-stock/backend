package pl.maropce.paperstock.appUser.internal.exception;

import org.springframework.http.HttpStatus;
import pl.maropce.paperstock.exception.BaseException;

public class InvalidOldPasswordException extends BaseException {
    public InvalidOldPasswordException(String username) {
        super(HttpStatus.BAD_REQUEST, String.format("Invalid old password for user: %s", username));
    }
}
