package pl.maropce.paperstock.alpaca;

import java.math.BigDecimal;
import java.time.Instant;

public record BarDTO(
        BigDecimal o,
        BigDecimal h,
        BigDecimal l,
        BigDecimal c,
        Instant t
) {
}
