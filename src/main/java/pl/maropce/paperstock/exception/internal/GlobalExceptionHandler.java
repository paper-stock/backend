package pl.maropce.paperstock.exception.internal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.maropce.paperstock.exception.BaseException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ExceptionResolver> handleBaseException(BaseException ex) {
        ExceptionResolver exceptionResolver = new ExceptionResolver(ex);

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(exceptionResolver);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResolver> handle(MethodArgumentNotValidException ex) {
        final String message = "Invalid Arguments";

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        BaseException baseException = new BaseException(HttpStatus.BAD_REQUEST, message);

        ExceptionResolver resolver = new InvalidArgumentsExceptionResolver(baseException, errors);
        return ResponseEntity
                .status(baseException.getStatusCode())
                .body(resolver);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResolver> handleUsernameNotFoundException(BadCredentialsException ex) {

        BaseException baseException = new BaseException(HttpStatus.UNAUTHORIZED, ex.getMessage());

        ExceptionResolver resolver = new ExceptionResolver(baseException);
        return ResponseEntity
                .status(baseException.getStatusCode())
                .body(resolver);
    }
}
