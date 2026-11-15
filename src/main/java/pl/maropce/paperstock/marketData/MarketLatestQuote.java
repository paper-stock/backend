package pl.maropce.paperstock.marketData;

import java.math.BigDecimal;
import java.time.Instant;

public record MarketLatestQuote(
        Instant timestamp,
        BigDecimal askPrice,
        BigDecimal bidPrice,
        long askSize,
        long bidSize
) {
}
