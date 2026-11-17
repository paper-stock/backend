package pl.maropce.paperstock.payment;

import java.math.BigDecimal;
import java.util.UUID;

public record FundsDepositedEvent(
        UUID depositId,
        UUID appUserId,
        BigDecimal amount,
        String currency
) {
}
