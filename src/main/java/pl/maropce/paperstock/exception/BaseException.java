package pl.maropce.paperstock.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {
    private final HttpStatus statusCode;

    public BaseException(
            HttpStatus statusCode,
            String message
    ) {
        super(message);
        this.statusCode = statusCode;
    }
}
