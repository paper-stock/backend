package pl.maropce.paperstock.alpaca.response;

public record AlpacaStockSnapshotResponse(
        AlpacaBarResponse dailyBar,
        AlpacaLatestQuoteResponse latestQuote,
        AlpacaLatestTradeResponse latestTrade,
        AlpacaBarResponse minuteBar,
        AlpacaBarResponse prevDailyBar
) {
}