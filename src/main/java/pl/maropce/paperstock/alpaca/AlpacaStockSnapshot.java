package pl.maropce.paperstock.alpaca;

import pl.maropce.paperstock.alpaca.response.LatestQuote;
import pl.maropce.paperstock.alpaca.response.LatestTrade;

public record AlpacaStockSnapshot(
        AlpacaBar dailyBar,
        LatestQuote latestQuote,
        LatestTrade latestTrade,
        AlpacaBar minuteBar,
        AlpacaBar prevDailyBar
) {
}