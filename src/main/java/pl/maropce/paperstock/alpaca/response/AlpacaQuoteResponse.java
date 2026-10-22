package pl.maropce.paperstock.alpaca.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

public record AlpacaQuoteResponse(
        @JsonProperty("ap") BigDecimal askPrice,
        @JsonProperty("as") long askSize,

        @JsonProperty("bp") BigDecimal bidPrice,
        @JsonProperty("bs") long bidSize,

        @JsonProperty("t") Instant timestamp
) {
}