package pl.maropce.paperstock.appUser.internal.exception;

import org.springframework.http.HttpStatus;
import pl.maropce.paperstock.exception.BaseException;

public class NotEnoughFundsException extends BaseException {
    public NotEnoughFundsException() {
        super(HttpStatus.CONFLICT, "Not enough funds to perform this operation");
    }
}
