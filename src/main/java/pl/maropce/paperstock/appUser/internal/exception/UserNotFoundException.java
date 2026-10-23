package pl.maropce.paperstock.appUser.internal.exception;

import org.springframework.http.HttpStatus;
import pl.maropce.paperstock.exception.BaseException;

import java.util.UUID;

public class UserNotFoundException extends BaseException {

    public UserNotFoundException(UUID id) {
        super(HttpStatus.NOT_FOUND, String.format("User with id: %s does not exists", id));
    }
}
