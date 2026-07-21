package pl.maropce.paperstock.exception.internal;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import pl.maropce.paperstock.exception.BaseException;

@Getter
@Setter
public class ExceptionResolver {

    private HttpStatus statusCode;
    private String message;

    public ExceptionResolver(BaseException exception) {
        statusCode = exception.getStatusCode();
        message = exception.getMessage();

    }
}
