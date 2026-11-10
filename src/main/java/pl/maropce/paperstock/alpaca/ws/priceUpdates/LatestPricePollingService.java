package pl.maropce.paperstock.alpaca.ws.priceUpdates;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.maropce.paperstock.alpaca.ws.ClientSessionRegistry;
import pl.maropce.paperstock.alpaca.ws.SymbolSubscriptionManager;
import pl.maropce.paperstock.stock.StockApi;
import pl.maropce.paperstock.stock.StockSnapshotDTO;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

@Component
@Slf4j
public class LatestPricePollingService {

    private final ClientSessionRegistry clientSessionRegistry;
    private final SymbolSubscriptionManager symbolSubscriptionManager;
    private final ObjectMapper objectMapper;
    private final StockApi stockApi;

    public LatestPricePollingService(
            @Qualifier("priceUpdatesSessionRegistry") ClientSessionRegistry clientSessionRegistry,
            @Qualifier("priceUpdatesSubscriptionManager") SymbolSubscriptionManager symbolSubscriptionManager,
            ObjectMapper objectMapper,
            StockApi stockApi
    ) {
        this.clientSessionRegistry = clientSessionRegistry;
        this.symbolSubscriptionManager = symbolSubscriptionManager;
        this.objectMapper = objectMapper;
        this.stockApi = stockApi;
    }


    @Scheduled(fixedDelay = 5000)
    public void pollLatestPrice() {

        Set<String> allSymbols = symbolSubscriptionManager.allSubscribedSymbols();
        if (allSymbols.isEmpty()) {
            log.debug("No active subscriptions for price updates... skipping polling");
            return;
        }

        log.debug("Polling latest price for symbol");

        List<StockSnapshotDTO> snapshots = stockApi.getSnapshots(new ArrayList<>(allSymbols));

        Map<String, Map<String, StockSnapshotDTO>> updatesByClient =
                new HashMap<>();

        snapshots.forEach((snapshotDTO) -> {
            for (String clientId : symbolSubscriptionManager.clientsFor(snapshotDTO.symbol())) {
                updatesByClient
                        .computeIfAbsent(clientId, _ -> new HashMap<>())
                        .put(snapshotDTO.symbol(), snapshotDTO);
            }
        });

        updatesByClient.forEach((clientId, prices) -> {
            String message = objectMapper.writeValueAsString(
                    prices.values()
            );

            clientSessionRegistry.sendMessage(clientId, message);
        });

        log.error("Snapshots sent to {} clients for {} symbols", updatesByClient.size(), allSymbols.size());
    }
}
