package pl.maropce.paperstock.marketData;

import java.math.BigDecimal;
import java.time.Instant;

public record MarketQuote(

        BigDecimal askPrice,
        long askSize,

        BigDecimal bidPrice,
        long bidSize,

        Instant timestamp
) {
}
