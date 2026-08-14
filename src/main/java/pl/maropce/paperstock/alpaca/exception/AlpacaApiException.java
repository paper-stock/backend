package pl.maropce.paperstock.alpaca.exception;

import org.springframework.http.HttpStatus;
import pl.maropce.paperstock.exception.BaseException;

public class AlpacaApiException extends BaseException {
    public AlpacaApiException(HttpStatus status, String message) {
        super(status, message);
    }
}
