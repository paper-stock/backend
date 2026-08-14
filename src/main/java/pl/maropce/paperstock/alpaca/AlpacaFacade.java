package pl.maropce.paperstock.alpaca;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.maropce.paperstock.alpaca.internal.AlpacaMapper;
import pl.maropce.paperstock.alpaca.internal.AlpacaMarketDataClient;
import pl.maropce.paperstock.alpaca.internal.AlpacaTradingClient;
import pl.maropce.paperstock.alpaca.response.AlpacaMostActiveStockResponse;
import pl.maropce.paperstock.alpaca.response.AlpacaStockSnapshotResponse;
import pl.maropce.paperstock.alpaca.response.AlpacaTopMoversResponse;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlpacaFacade {

    private final AlpacaMarketDataClient alpacaMarketDataClient;
    private final AlpacaTradingClient alpacaTradingClient;
    private final AlpacaMapper alpacaMapper;

    public List<MarketBar> getBars(
            String symbol,
            String timeframe,
            Instant from,
            Instant to
    ) {
        return alpacaMarketDataClient.getBars(symbol, timeframe, from, to)
                .stream()
                .map(alpacaMapper::toMarketBar)
                .toList();
    }

    public List<String> getMostActiveStocks(int top) {
        return alpacaMarketDataClient.getMostActiveStocks(top).mostActives()
                .stream()
                .map(AlpacaMostActiveStockResponse::symbol)
                .toList();
    }

    public Map<String, MarketStockSnapshot> getSnapshots(List<String> symbols) {

        Map<String, AlpacaStockSnapshotResponse> snapshots =
                alpacaMarketDataClient.getSnapshots(symbols);

        return snapshots.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> alpacaMapper.toMarketStockSnapshot(
                                entry.getKey(),
                                entry.getValue()
                        )
                ));
    }

    public List<MarketStock> getAllAssets() {
        return alpacaTradingClient.getAllAssets()
                .stream()
                .map(alpacaMapper::toMarketStock)
                .toList();
    }

    public MarketTopMovers getTopMarketMovers(int top) {
        AlpacaTopMoversResponse topMovers = alpacaMarketDataClient.getTopMovers(top);
        return alpacaMapper.toMarketTopMovers(topMovers);
    }

}
