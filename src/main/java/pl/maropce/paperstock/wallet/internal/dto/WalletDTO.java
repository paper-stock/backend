package pl.maropce.paperstock.wallet.internal.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletDTO(
        UUID id,
        UUID appUserId,
        BigDecimal balance
) {
}
