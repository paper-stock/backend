package pl.maropce.paperstock.alpaca;

import java.util.List;

public record MarketQuotes(
        String symbol,
        List<MarketQuote> quotes
) {
}
