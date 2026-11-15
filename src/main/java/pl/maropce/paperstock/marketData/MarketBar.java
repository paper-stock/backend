package pl.maropce.paperstock.marketData;

import java.math.BigDecimal;
import java.time.Instant;

public record MarketBar(
        BigDecimal open,
        BigDecimal high,
        BigDecimal low,
        BigDecimal close,
        Instant timestamp
) {
}
