package pl.maropce.paperstock.order.internal.exception;

import org.springframework.http.HttpStatus;
import pl.maropce.paperstock.exception.BaseException;

public class InvalidOrderException extends BaseException {
    public InvalidOrderException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
