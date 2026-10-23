package pl.maropce.paperstock.auth.internal.exception;

import org.springframework.http.HttpStatus;
import pl.maropce.paperstock.exception.BaseException;

public class AccountAlreadyExistsException extends BaseException {

    public AccountAlreadyExistsException(String email) {
        super(HttpStatus.CONFLICT, String.format("Account with email %s already exists.", email));
    }
}
