package pl.maropce.paperstock.order.internal.exception;

import org.springframework.http.HttpStatus;
import pl.maropce.paperstock.exception.BaseException;

import java.util.UUID;

public class OrderNotFoundException extends BaseException {
    public OrderNotFoundException(UUID uuid) {
        super(HttpStatus.NOT_FOUND, "Order not found: " + uuid);
    }
}
