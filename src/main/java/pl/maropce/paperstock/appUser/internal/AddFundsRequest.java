package pl.maropce.paperstock.appUser.internal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AddFundsRequest(

        @NotNull
        @DecimalMin(value = "1.00")
        @Digits(integer = 12, fraction = 2)
        BigDecimal amount
) {
}
