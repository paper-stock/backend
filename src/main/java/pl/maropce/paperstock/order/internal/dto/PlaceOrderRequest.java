package pl.maropce.paperstock.order.internal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import pl.maropce.paperstock.order.internal.OrderSide;
import pl.maropce.paperstock.order.internal.OrderType;

import java.math.BigDecimal;

public record PlaceOrderRequest(

        @NotNull
        OrderSide orderSide,

        @NotNull
        OrderType orderType,

        @NotNull
        String stockSymbol,

        @DecimalMin(value = "0.01")
        @Digits(integer = 12, fraction = 2)
        BigDecimal funds,

        @DecimalMin(value = "0.0001")
        @Digits(integer = 12, fraction = 9)
        BigDecimal shares,

        @DecimalMin(value = "0.01")
        @Digits(integer = 12, fraction = 2)
        BigDecimal limitPrice,

        @DecimalMin(value = "0.01")
        @Digits(integer = 12, fraction = 2)
        BigDecimal stopPrice
) {
}
