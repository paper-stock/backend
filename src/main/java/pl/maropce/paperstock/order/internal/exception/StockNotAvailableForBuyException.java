package pl.maropce.paperstock.order.internal.exception;

import org.springframework.http.HttpStatus;
import pl.maropce.paperstock.exception.BaseException;

public class StockNotAvailableForBuyException extends BaseException {
    public StockNotAvailableForBuyException() {
        super(HttpStatus.UNPROCESSABLE_CONTENT, "Stock is not tradable for buying");
    }
}
