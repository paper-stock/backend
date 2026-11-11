package pl.maropce.paperstock.alpaca.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record AlpacaTopMoverResponse(
        @JsonProperty("percent_change")
        BigDecimal percentChange,

        BigDecimal change,
        BigDecimal price,
        String symbol
) {
}
