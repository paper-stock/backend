package pl.maropce.paperstock.alpaca;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class AlpacaMarketDataClient {

    private final WebClient webDataClient;

    public AlpacaMarketDataClient(
            @Qualifier("alpacaDataWebClient") WebClient webDataClient
    ) {
        this.webDataClient = webDataClient;
    }

    public List<AlpacaBar> getBars(
            String symbol,
            String timeframe,
            Instant from,
            Instant to
    ) {
        List<AlpacaBar> results = new ArrayList<>();

        String pageToken = null;

        int requests = 0;

        do {
            final String nextPageToken = pageToken;
            AlpacaBarsResponse response = webDataClient.get()
                    .uri(uri -> {
                                URI build = uri.path("/v2/stocks/bars")
                                        .queryParam("symbols", symbol)
                                        .queryParam("timeframe", timeframe)
                                        .queryParam("limit", 10000)
                                        .queryParam("feed", "sip")
                                        .queryParam("start", from)
                                        .queryParam("end", to)
                                        .queryParamIfPresent("page_token", Optional.ofNullable(nextPageToken))
                                        .build();

                                log.info("url: {}", build);

                                return build;
                            }
                    )
                    .retrieve()
                    .bodyToMono(AlpacaBarsResponse.class)
                    .blockOptional()
                    .orElseThrow(() -> new IllegalStateException("Failed to fetch bars for symbol: " + symbol));
            requests++;

            results.addAll(
                    response.bars()
                            .getOrDefault(symbol, List.of())
            );


            pageToken = response.nextPageToken();
        } while (pageToken != null);

        log.info("Total requests for {}: {}", symbol, requests);
        return results;
    }

    public Map<String, AlpacaStockSnapshot> getSnapshots(List<String> symbols) {

        return webDataClient.get()
                .uri(uri -> {
                            URI build = uri
                                    .path("/v2/stocks/snapshots")
                                    .queryParam("feed", "delayed_sip")
                                    .queryParam("symbols", String.join(",", symbols))
                                    .build();
                            log.info("url: {}", build);
                            return build;
                        }
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, AlpacaStockSnapshot>>() {
                })
                .block();
    }

    public AlpacaMostActiveStocksResponse getMostActiveStocks(int top) {
        return webDataClient.get()
                .uri(uri -> uri
                        .path("/v1beta1/screener/stocks/most-actives")
                        .queryParam("by", "trades")
                        .queryParam("top", top)
                        .build()
                )
                .retrieve()
                .bodyToMono(AlpacaMostActiveStocksResponse.class)
                .block();
    }
}
