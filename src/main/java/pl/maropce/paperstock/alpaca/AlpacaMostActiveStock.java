package pl.maropce.paperstock.alpaca;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AlpacaMostActiveStock(
        String symbol,

        @JsonProperty("trade_count")
        long tradeCount,

        long volume
) {
}