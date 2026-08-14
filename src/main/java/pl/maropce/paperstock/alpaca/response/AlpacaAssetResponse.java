package pl.maropce.paperstock.alpaca.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record AlpacaAssetResponse(

        @JsonProperty("id")
        UUID externalId,

        @JsonProperty("class")
        String assetClass,

        String exchange,
        String symbol,
        String name,
        String status,
        boolean tradable
) {
}
