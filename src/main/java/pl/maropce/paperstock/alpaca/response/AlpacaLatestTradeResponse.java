package pl.maropce.paperstock.alpaca.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record AlpacaLatestTradeResponse(
        List<String> c,
        long i,
        BigDecimal p,
        long s,
        Instant t,
        String x,
        String z
) {
}