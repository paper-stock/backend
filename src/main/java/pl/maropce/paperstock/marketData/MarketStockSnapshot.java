package pl.maropce.paperstock.marketData;

public record MarketStockSnapshot(
        String symbol,
        MarketBar dailyBar,
        MarketLatestQuote latestQuote,
        MarketLatestTrade latestTrade,
        MarketBar minuteBar,
        MarketBar prevDailyBar
) {
}
