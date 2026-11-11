package pl.maropce.paperstock.alpaca.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AlpacaMostActiveStockResponse(
        String symbol,

        @JsonProperty("trade_count")
        long tradeCount,

        long volume
) {
}