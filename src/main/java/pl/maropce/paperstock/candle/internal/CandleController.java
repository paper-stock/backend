package pl.maropce.paperstock.candle.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.maropce.paperstock.candle.internal.dto.CandleDTO;

import java.util.List;

@RestController
@RequestMapping("/api/candles/{symbol}")
@RequiredArgsConstructor
public class CandleController {

    private final CandleService candleService;

    @GetMapping
    public List<CandleDTO> getCandles(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "MONTH_1") Timeframe timeframe
    ) {
        return candleService.getCandles(symbol, timeframe);
    }
}
