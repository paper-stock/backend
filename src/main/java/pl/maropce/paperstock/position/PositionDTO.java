package pl.maropce.paperstock.position;

import java.math.BigDecimal;
import java.util.UUID;

public record PositionDTO(
        UUID id,
        UUID stockId,
        String stockSymbol,
        BigDecimal shares,
        BigDecimal reservedShares
) {
}
