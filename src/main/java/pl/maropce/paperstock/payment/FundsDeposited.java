package pl.maropce.paperstock.payment;

import java.math.BigDecimal;
import java.util.UUID;

public record FundsDeposited(
        UUID depositId,
        UUID userId,
        BigDecimal amount,
        String currency
) {
}
