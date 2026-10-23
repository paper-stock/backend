package pl.maropce.paperstock.jwt.internal.refreshToken.exception;

import org.springframework.http.HttpStatus;
import pl.maropce.paperstock.exception.BaseException;

public class InvalidRefreshTokenException extends BaseException {
    public InvalidRefreshTokenException() {
        super(HttpStatus.UNAUTHORIZED, "Invalid refresh token.");
    }
}
