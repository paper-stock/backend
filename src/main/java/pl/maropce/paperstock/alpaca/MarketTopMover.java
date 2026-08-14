package pl.maropce.paperstock.alpaca;

import java.math.BigDecimal;

public record MarketTopMover(
        BigDecimal percentChange,
        BigDecimal change,
        BigDecimal price,
        String symbol

) {
}
