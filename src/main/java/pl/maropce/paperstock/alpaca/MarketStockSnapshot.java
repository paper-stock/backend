package pl.maropce.paperstock.alpaca;

public record MarketStockSnapshot(
        String symbol,
        MarketBar dailyBar,
        MarketLatestQuote latestQuote,
        MarketLatestTrade latestTrade,
        MarketBar minuteBar,
        MarketBar prevDailyBar
) {
}
