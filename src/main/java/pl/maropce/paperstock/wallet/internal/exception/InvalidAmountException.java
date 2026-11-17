package pl.maropce.paperstock.wallet.internal.exception;

import org.springframework.http.HttpStatus;
import pl.maropce.paperstock.exception.BaseException;

import java.math.BigDecimal;

public class InvalidAmountException extends BaseException {
    public InvalidAmountException(BigDecimal amount) {
        super(HttpStatus.BAD_REQUEST, "Invalid amount: " + amount + ". Amount must be positive.");
    }
}
