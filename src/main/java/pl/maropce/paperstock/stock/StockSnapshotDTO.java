package pl.maropce.paperstock.stock;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record StockSnapshotDTO(
        String symbol,
        BigDecimal price,
        BigDecimal amountChange,
        BigDecimal percentageChange
) {
}
