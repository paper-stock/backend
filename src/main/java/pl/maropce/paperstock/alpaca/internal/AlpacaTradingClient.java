package pl.maropce.paperstock.alpaca.internal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import pl.maropce.paperstock.alpaca.response.AlpacaAssetResponse;

import java.util.List;

@Component
public class AlpacaTradingClient {

    private final RestClient alpacaTradingRestClient;

    public AlpacaTradingClient(
            @Qualifier("alpacaTradingRestClient") RestClient alpacaTradingRestClient
    ) {
        this.alpacaTradingRestClient = alpacaTradingRestClient;
    }

    public List<AlpacaAssetResponse> getAllAssets() {
        return alpacaTradingRestClient.get()
                .uri(uri -> uri
                        .path("/assets")
                        .queryParam("status", "active")
                        .queryParam("asset_class", "us_equity")
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}
