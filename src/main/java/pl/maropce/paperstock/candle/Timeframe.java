package pl.maropce.paperstock.candle;

import lombok.Getter;

@Getter
public enum Timeframe {
    MIN_1("1T"),
    MIN_5("5T"),
    MIN_10("10T"),
    MIN_15("15T"),
    MIN_30("30T"),
    HOUR_1("1H"),
    HOUR_4("4H"),
    DAY_1("1D"),
    WEEK_1("1W"),
    MONTH_1("1M");

    private final String value;

    Timeframe(String timeframeValue) {
        this.value = timeframeValue;
    }
}
