package pl.maropce.paperstock.position;

import java.math.BigDecimal;
import java.util.UUID;

public record AddNewPositionRequest(
        UUID appUserId,
        UUID stockId,
        String stockSymbol,
        BigDecimal shares
) {
}
