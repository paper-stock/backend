package pl.maropce.paperstock.appUser.internal.exception;

import org.springframework.http.HttpStatus;
import pl.maropce.paperstock.exception.BaseException;

public class InvalidOldPasswordException extends BaseException {
    public InvalidOldPasswordException() {
        super(HttpStatus.BAD_REQUEST, "Invalid old password");
    }
}
