package pl.maropce.paperstock.candle;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.maropce.paperstock.candle.internal.CandleService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CandleApi {

    private final CandleService candleService;

    public List<CandleDTO> getCandles(String symbol, Timeframe timeframe) {
        return candleService.getCandles(symbol, timeframe);
    }

}
