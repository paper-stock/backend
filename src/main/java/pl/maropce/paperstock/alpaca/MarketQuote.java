package pl.maropce.paperstock.alpaca;

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
