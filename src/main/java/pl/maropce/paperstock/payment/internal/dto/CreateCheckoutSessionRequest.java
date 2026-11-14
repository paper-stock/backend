package pl.maropce.paperstock.payment.internal.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateCheckoutSessionRequest(
        @Min(5)
        @Max(100000)
        @Digits(integer = 6, fraction = 0)
        @NotNull
        BigDecimal amount
) {
}
