package pl.maropce.paperstock.stock.internal.exception;

import org.springframework.http.HttpStatus;
import pl.maropce.paperstock.exception.BaseException;

public class StockNotFoundException extends BaseException {

    public StockNotFoundException(String stockSymbol) {
        super(HttpStatus.NOT_FOUND, String.format("Stock with symbol %s not found.", stockSymbol));
    }
}
