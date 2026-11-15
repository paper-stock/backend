package pl.maropce.paperstock.marketData;

import java.util.List;

public record MarketQuotes(
        String symbol,
        List<MarketQuote> quotes
) {
}
