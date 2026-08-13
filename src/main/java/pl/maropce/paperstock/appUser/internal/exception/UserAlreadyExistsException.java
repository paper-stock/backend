package pl.maropce.paperstock.appUser.internal.exception;

import org.springframework.http.HttpStatus;
import pl.maropce.paperstock.exception.BaseException;

public class UserAlreadyExistsException extends BaseException {

    public UserAlreadyExistsException(String email) {
        super(HttpStatus.CONFLICT, String.format("User with email %s already exists.", email));
    }
}
