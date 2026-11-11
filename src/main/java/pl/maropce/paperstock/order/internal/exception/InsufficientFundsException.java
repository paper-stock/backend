package pl.maropce.paperstock.order.internal.exception;

import org.springframework.http.HttpStatus;
import pl.maropce.paperstock.exception.BaseException;

import java.util.UUID;

public class InsufficientFundsException extends BaseException {
    public InsufficientFundsException(UUID appUserId) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, "Insufficient funds for appUser with ID: " + appUserId);
    }
}
