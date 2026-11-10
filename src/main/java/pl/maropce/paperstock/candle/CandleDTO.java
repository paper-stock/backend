package pl.maropce.paperstock.candle;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

public record CandleDTO(
        String symbol,
        Timeframe timeframe,

        @JsonProperty("o")
        BigDecimal open,

        @JsonProperty("h")
        BigDecimal high,

        @JsonProperty("l")
        BigDecimal low,

        @JsonProperty("c")
        BigDecimal close,

        @JsonProperty("t")
        Instant timestamp
) {
}
