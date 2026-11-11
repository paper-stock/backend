package pl.maropce.paperstock.alpaca;

import java.math.BigDecimal;
import java.time.Instant;

public record MarketLatestTrade(
        String symbol,
        long i,
        BigDecimal p,
        long s,
        Instant t
) {
}
