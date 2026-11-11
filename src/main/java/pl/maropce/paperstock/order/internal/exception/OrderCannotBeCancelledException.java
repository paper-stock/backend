package pl.maropce.paperstock.order.internal.exception;

import org.springframework.http.HttpStatus;
import pl.maropce.paperstock.exception.BaseException;
import pl.maropce.paperstock.order.internal.OrderStatus;

public class OrderCannotBeCancelledException extends BaseException {
    public OrderCannotBeCancelledException(OrderStatus orderStatus) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, "Order cannot be cancelled with status: " + orderStatus);
    }
}
