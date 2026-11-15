package pl.maropce.paperstock.payment.internal.exception;

import org.springframework.http.HttpStatus;
import pl.maropce.paperstock.exception.BaseException;

public class PaymentProviderException extends BaseException {

    public PaymentProviderException() {
        super(HttpStatus.BAD_GATEWAY, "Error occurred while processing payment");
    }
}
