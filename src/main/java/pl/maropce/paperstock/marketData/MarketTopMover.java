package pl.maropce.paperstock.marketData;

import java.math.BigDecimal;

public record MarketTopMover(
        BigDecimal percentChange,
        BigDecimal change,
        BigDecimal price,
        String symbol

) {
}
