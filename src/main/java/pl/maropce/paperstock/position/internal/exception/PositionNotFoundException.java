package pl.maropce.paperstock.position.internal.exception;

import org.springframework.http.HttpStatus;
import pl.maropce.paperstock.exception.BaseException;

import java.util.UUID;

public class PositionNotFoundException extends BaseException {
    public PositionNotFoundException(UUID appUserId, UUID stockId) {
        super(HttpStatus.NOT_FOUND, "Position not found for stock with ID: " + stockId + " and appUser with ID: " + appUserId);
    }
}
