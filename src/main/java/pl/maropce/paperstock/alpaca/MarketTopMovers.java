package pl.maropce.paperstock.alpaca;

import java.time.Instant;
import java.util.List;

public record MarketTopMovers(
        Instant lastUpdated,
        List<MarketTopMover> gainers,
        List<MarketTopMover> losers
) {
}
