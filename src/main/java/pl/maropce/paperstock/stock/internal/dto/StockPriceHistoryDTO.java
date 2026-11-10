package pl.maropce.paperstock.stock.internal.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record StockPriceHistoryDTO(
        String symbol,
        BigDecimal day,
        BigDecimal week,
        BigDecimal month,
        BigDecimal quarter,
        BigDecimal year
) {
}
