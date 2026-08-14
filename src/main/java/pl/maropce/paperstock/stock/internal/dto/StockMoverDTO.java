package pl.maropce.paperstock.stock.internal.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record StockMoverDTO(
        UUID id,
        String exchange,
        String symbol,
        String name,
        BigDecimal price,
        BigDecimal percentChange,
        BigDecimal change,
        String logoUrl

) {
}
