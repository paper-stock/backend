package pl.maropce.paperstock.alpaca.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record AlpacaTopMoversResponse(
        @JsonProperty("last_updated")
        Instant lastUpdated,
        List<AlpacaTopMoverResponse> gainers,
        List<AlpacaTopMoverResponse> losers
) {
}
