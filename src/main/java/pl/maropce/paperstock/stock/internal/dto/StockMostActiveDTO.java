package pl.maropce.paperstock.stock.internal.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record StockMostActiveDTO(
        UUID id,
        String exchange,
        String symbol,
        String name,
        BigDecimal price,
        BigDecimal percentageChange,
        String logoUrl
) {
}
