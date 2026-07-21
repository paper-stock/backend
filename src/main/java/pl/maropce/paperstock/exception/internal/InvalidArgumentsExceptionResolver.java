package pl.maropce.paperstock.exception.internal;

import lombok.Getter;
import lombok.Setter;
import pl.maropce.paperstock.exception.BaseException;

import java.util.Map;

@Getter
@Setter
public class InvalidArgumentsExceptionResolver extends ExceptionResolver {

    private Map<String, String> errors;

    public InvalidArgumentsExceptionResolver(BaseException baseException, Map<String, String> errors) {
        super(baseException);
        this.errors = errors;
    }
}