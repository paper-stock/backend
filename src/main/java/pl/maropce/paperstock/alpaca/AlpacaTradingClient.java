package pl.maropce.paperstock.alpaca;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class AlpacaTradingClient {

    private final WebClient webTradingClient;

    public AlpacaTradingClient(
            @Qualifier("alpacaTradingWebClient") WebClient webClient
    ) {
        this.webTradingClient = webClient;
    }

    public List<AlpacaAssetResponse> getAllAssets() {
        return webTradingClient.get()
                .uri(uri -> uri
                        .path("/assets")
                        .queryParam("status", "active")
                        .queryParam("asset_class", "us_equity")
                        .build())
                .retrieve()
                .bodyToFlux(AlpacaAssetResponse.class)
                .collectList()
                .block();
    }
}
