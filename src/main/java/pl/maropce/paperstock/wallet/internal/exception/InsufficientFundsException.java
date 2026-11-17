package pl.maropce.paperstock.wallet.internal.exception;

import org.springframework.http.HttpStatus;
import pl.maropce.paperstock.exception.BaseException;

import java.math.BigDecimal;

public class InsufficientFundsException extends BaseException {
    public InsufficientFundsException(BigDecimal amount, BigDecimal balance) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, "Insufficient funds: requested amount %s, available balance %s"
                .formatted(amount, balance));

    }
}
