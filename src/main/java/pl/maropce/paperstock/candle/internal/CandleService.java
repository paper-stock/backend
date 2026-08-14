package pl.maropce.paperstock.candle.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.maropce.paperstock.alpaca.AlpacaFacade;
import pl.maropce.paperstock.candle.internal.dto.CandleDTO;
import pl.maropce.paperstock.candle.internal.dto.CandleMapper;
import pl.maropce.paperstock.util.MarketClock;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
class CandleService {

    private final AlpacaFacade alpacaFacade;
    private final CandleRepository candleRepository;
    private final CandleMapper candleMapper;
    private final MarketClock marketClock;

    @Transactional
    public List<CandleDTO> getCandles(String symbol, Timeframe timeframe) {

        Instant from = calculateFrom(timeframe).truncatedTo(ChronoUnit.MINUTES);
        Instant to = marketClock.now().truncatedTo(ChronoUnit.MINUTES);

        List<Candle> result;

        if (isCacheCoverageSufficient(symbol, timeframe, from, to)) {
            result = candleRepository
                    .findBySymbolAndTimeframeAndTimestampBetweenOrderByTimestampAsc(symbol, timeframe, from, to);
        } else {
            List<Candle> cached = candleRepository
                    .findBySymbolAndTimeframeAndTimestampBetweenOrderByTimestampAsc(symbol, timeframe, from, to);
            result = fetchMissingAndStore(symbol, timeframe, from, to, cached);
        }

        return result.stream()
                .map(candleMapper::toDto)
                .toList();
    }

    private boolean isCacheCoverageSufficient(String symbol, Timeframe timeframe, Instant from, Instant to) {
        Optional<Candle> oldest = candleRepository.findFirstBySymbolAndTimeframeOrderByTimestampAsc(symbol, timeframe);
        Optional<Candle> newest = candleRepository.findFirstBySymbolAndTimeframeOrderByTimestampDesc(symbol, timeframe);

        if (oldest.isEmpty() || newest.isEmpty()) {
            return false;
        }

        boolean coversStart = !oldest.get().getTimestamp().isAfter(from);
        boolean coversEnd = !newest.get().getTimestamp().isBefore(to.minus(stalenessTolerance(timeframe)));
        return coversStart && coversEnd;
    }

    private List<Candle> fetchMissingAndStore(
            String symbol, Timeframe timeframe, Instant from, Instant to, List<Candle> cached
    ) {
        List<Candle> bars = alpacaFacade.getBars(symbol, timeframe.getValue(), from, to)
                .stream()
                .map(marketBar -> candleMapper.toEntity(marketBar, symbol, timeframe))
                .toList();

        log.info("Fetched {} bars", bars.size());

        Set<Instant> alreadyCached = cached.stream()
                .map(Candle::getTimestamp)
                .collect(Collectors.toSet());

        List<Candle> newCandles = bars.stream()
                .filter(bar -> !alreadyCached.contains(bar.getTimestamp()))
                .toList();

        List<Candle> saved = candleRepository.saveAll(newCandles);

        List<Candle> merged = new ArrayList<>(cached);
        merged.addAll(saved);

        return merged.stream()
                .sorted(Comparator.comparing(Candle::getTimestamp))
                .toList();
    }

    private Instant calculateFrom(Timeframe timeframe) {
        Instant now = marketClock.now();
        return switch (timeframe) {
            case MONTH_1 -> now.minus(20 * 365, ChronoUnit.DAYS);
            case WEEK_1 -> now.minus(10 * 365, ChronoUnit.DAYS);
            case DAY_1 -> now.minus(2 * 365, ChronoUnit.DAYS);
            case HOUR_4 -> now.minus(180, ChronoUnit.DAYS);
            case HOUR_1 -> now.minus(90, ChronoUnit.DAYS);
            case MIN_30 -> now.minus(30, ChronoUnit.DAYS);
            case MIN_15 -> now.minus(15, ChronoUnit.DAYS);
            case MIN_10 -> now.minus(7, ChronoUnit.DAYS);
            case MIN_5 -> now.minus(3, ChronoUnit.DAYS);
            case MIN_1 -> now.minus(1, ChronoUnit.DAYS);
        };
    }

    private Duration stalenessTolerance(Timeframe timeframe) {
        return switch (timeframe) {
            case MONTH_1 -> Duration.ofDays(30);
            case WEEK_1 -> Duration.ofDays(7);
            case DAY_1 -> Duration.ofDays(1);
            case HOUR_4 -> Duration.ofHours(4);
            case HOUR_1 -> Duration.ofHours(1);
            case MIN_30 -> Duration.ofMinutes(30);
            case MIN_15 -> Duration.ofMinutes(15);
            case MIN_10 -> Duration.ofMinutes(10);
            case MIN_5 -> Duration.ofMinutes(5);
            case MIN_1 -> Duration.ofMinutes(0);
        };
    }
}
