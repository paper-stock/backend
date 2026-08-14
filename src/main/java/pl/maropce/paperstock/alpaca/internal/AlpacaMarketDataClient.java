package pl.maropce.paperstock.alpaca.internal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import pl.maropce.paperstock.alpaca.response.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class AlpacaMarketDataClient {

    @Value("${alpaca.max-pages:10}")
    private int maxPages;

    private final RestClient dataRestClient;

    public AlpacaMarketDataClient(
            @Qualifier("alpacaDataRestClient") RestClient dataRestClient
    ) {
        this.dataRestClient = dataRestClient;
    }

    public List<AlpacaBarResponse> getBars(
            String symbol,
            String timeframe,
            Instant from,
            Instant to
    ) {
        List<AlpacaBarResponse> results = new ArrayList<>();

        String pageToken = null;

        int requests = 0;

        do {
            final String nextPageToken = pageToken;
            AlpacaBarsResponse response = dataRestClient.get()
                    .uri(uri -> uri.path("/v2/stocks/bars")
                            .queryParam("symbols", symbol)
                            .queryParam("timeframe", timeframe)
                            .queryParam("limit", 10000)
                            .queryParam("feed", "sip")
                            .queryParam("start", from)
                            .queryParam("end", to)
                            .queryParamIfPresent("page_token", Optional.ofNullable(nextPageToken))
                            .build()
                    )
                    .retrieve()
                    .body(AlpacaBarsResponse.class);

            if (response == null || response.bars().isEmpty()) {
                log.info("No bars found for symbol: {}", symbol);
                break;
            }

            results.addAll(
                    response.bars()
                            .getOrDefault(symbol, List.of())
            );

            pageToken = response.nextPageToken();

            requests++;

            if (requests >= maxPages) {
                log.warn("Reached max page limit ({}) for symbol {}, timeframe {}. Remaining data may be incomplete.",
                        maxPages, symbol, timeframe);
                break;
            }

        } while (pageToken != null);

        log.info("Total requests for {}: {}", symbol, requests);
        return results;
    }

    public Map<String, AlpacaStockSnapshotResponse> getSnapshots(List<String> symbols) {

        return dataRestClient.get()
                .uri(uri -> uri
                        .path("/v2/stocks/snapshots")
                        .queryParam("feed", "delayed_sip")
                        .queryParam("symbols", String.join(",", symbols))
                        .build()
                )
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public AlpacaMostActiveStocksResponse getMostActiveStocks(int top) {
        return dataRestClient.get()
                .uri(uri -> uri
                        .path("/v1beta1/screener/stocks/most-actives")
                        .queryParam("by", "trades")
                        .queryParam("top", top)
                        .build()
                )
                .retrieve()
                .body(AlpacaMostActiveStocksResponse.class);
    }

    public AlpacaTopMoversResponse getTopMovers(int top) {
        return dataRestClient.get()
                .uri(uri -> uri
                        .path("/v1beta1/screener/stocks/movers")
                        .queryParam("top", top)
                        .build()
                )
                .retrieve()
                .body(AlpacaTopMoversResponse.class);
    }
}
