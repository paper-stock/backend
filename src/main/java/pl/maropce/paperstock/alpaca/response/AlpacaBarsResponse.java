package pl.maropce.paperstock.alpaca.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record AlpacaBarsResponse(
        Map<String, List<AlpacaBarResponse>> bars,

        @JsonProperty("next_page_token")
        String nextPageToken
) {
}