package pl.maropce.paperstock.marketData;

import java.time.Instant;
import java.util.List;

public record MarketTopMovers(
        Instant lastUpdated,
        List<MarketTopMover> gainers,
        List<MarketTopMover> losers
) {
}
