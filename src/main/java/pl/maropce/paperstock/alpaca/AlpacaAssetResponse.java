package pl.maropce.paperstock.alpaca;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record AlpacaAssetResponse(

        UUID id,

        @JsonProperty("class")
        String assetClass,

        String exchange,
        String symbol,
        String name,
        String status,
        boolean tradable
) {
}
