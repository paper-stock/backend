package pl.maropce.paperstock.order.internal.exception;

import org.springframework.http.HttpStatus;
import pl.maropce.paperstock.exception.BaseException;

public class InactiveStockException  extends BaseException {
    public InactiveStockException() {
        super(HttpStatus.UNPROCESSABLE_CONTENT, "Stock is inactive and cannot be traded.");
    }
}
