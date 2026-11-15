package pl.maropce.paperstock.marketData;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MarketDataApi {
    List<MarketBar> getBars(
            String symbol,
            String timeframe,
            Instant from,
            Instant to
    );

    List<String> getMostActiveStocks(int top);

    Map<String, MarketStockSnapshot> getSnapshots(List<String> symbols);

    List<MarketStock> getAllAssets();

    MarketTopMovers getTopMarketMovers(int top);

    Map<String, List<MarketQuote>> getQuotes(Set<String> symbols, Instant start, Instant end);
}
