package pl.maropce.paperstock.alpaca;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record AlpacaMostActiveStocksResponse(
        @JsonProperty("last_updated")
        Instant lastUpdated,

        @JsonProperty("most_actives")
        List<AlpacaMostActiveStock> mostActives
) {
}