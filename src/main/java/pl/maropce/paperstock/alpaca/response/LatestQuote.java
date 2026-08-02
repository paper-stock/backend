package pl.maropce.paperstock.alpaca.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

public record LatestQuote(
        Instant t,
        BigDecimal ap,
        BigDecimal bp,

        @JsonProperty("as")
        long askSize,

        @JsonProperty("bs")
        long bidSize
) {
}