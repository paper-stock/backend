package pl.maropce.paperstock.alpaca.response;

import java.math.BigDecimal;
import java.time.Instant;

public record AlpacaBarResponse(
        BigDecimal o,
        BigDecimal h,
        BigDecimal l,
        BigDecimal c,
        Instant t
) {
}
