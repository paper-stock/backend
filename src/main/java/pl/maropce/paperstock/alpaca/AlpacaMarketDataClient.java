package pl.maropce.paperstock.alpaca;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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

    public List<BarDTO> getBars(
            String symbol,
            String timeframe,
            Instant from,
            Instant to
    ) {
        List<BarDTO> results = new ArrayList<>();

        String pageToken = null;

        int requests = 0;

        do {
            final String nextPageToken = pageToken;
            AlpacaBarsResponse response = webDataClient.get()
                    .uri(uri -> {
                                URI build = uri.path("/stocks/bars")
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
}
