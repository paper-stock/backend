package pl.maropce.paperstock.stock.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.maropce.paperstock.alpaca.AlpacaFacade;
import pl.maropce.paperstock.alpaca.MarketStockSnapshot;
import pl.maropce.paperstock.alpaca.MarketTopMover;
import pl.maropce.paperstock.alpaca.MarketTopMovers;
import pl.maropce.paperstock.candle.CandleApi;
import pl.maropce.paperstock.candle.CandleDTO;
import pl.maropce.paperstock.candle.Timeframe;
import pl.maropce.paperstock.logodev.LogoService;
import pl.maropce.paperstock.stock.StockDTO;
import pl.maropce.paperstock.stock.StockSnapshotDTO;
import pl.maropce.paperstock.stock.internal.dto.*;
import pl.maropce.paperstock.stock.internal.exception.StockNotFoundException;
import pl.maropce.paperstock.util.MarketClock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {

    private final AlpacaFacade alpacaFacade;
    private final StockRepository stockRepository;
    private final StockMapper stockMapper;
    private final LogoService logoService;
    private final CandleApi candleApi;
    private final MarketClock marketClock;

    public List<StockDTO> search(String query, int logoSize) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        String normalizedQuery = query.trim().toLowerCase();

        List<Stock> stocks = stockRepository
                .searchActiveStocks(
                        normalizedQuery, normalizedQuery, normalizedQuery
                );

        log.debug("Found {} stocks for query '{}'", stocks.size(), normalizedQuery);

        return stocks
                .stream()
                .sorted(Comparator.comparingInt(stock -> score((Stock) stock, normalizedQuery))
                        .reversed())
                .limit(100)
                .map(stockMapper::toDTO)
                .map(dto -> dto.withLogoUrl(logoService.getLogoUrl(dto.symbol(), logoSize)))
                .toList();
    }

    private int score(Stock stock, String query) {
        String symbol = stock.getSymbol().toLowerCase();
        String name = stock.getName().toLowerCase();

        int score = 0;

        if (symbol.equals(query)) {
            score += 10000;
        }

        if (symbol.startsWith(query)) {
            score += 5000;
        }

        if (name.startsWith(query)) {
            score += 4000;
        }

        if (name.contains(query)) {
            score += 2000;
        }

        if (symbol.contains(query)) {
            score += 1000;
        }

        return score;
    }

    public List<StockMostActiveDTO> getMostActiveStocks(int top, int logoSize) {

        List<String> symbols = alpacaFacade.getMostActiveStocks(top);

        Map<String, Stock> stocksBySymbol = stockRepository
                .findAllBySymbolIn(symbols)
                .stream()
                .collect(Collectors.toMap(
                        Stock::getSymbol,
                        Function.identity()
                ));

        Map<String, StockSnapshotDTO> snapshots = getSnapshotsFromAlpaca(symbols)
                .stream()
                .collect(Collectors.toMap(
                        StockSnapshotDTO::symbol,
                        Function.identity()
                ));

        return symbols.stream()
                .map(symbol -> {
                    Stock stock = stocksBySymbol.get(symbol);
                    StockSnapshotDTO snapshot = snapshots.get(symbol);

                    if (stock == null || snapshot == null) {
                        log.warn("Symbol {} not found", symbol);
                        return null;
                    }

                    return new StockMostActiveDTO(
                            stock.getId(),
                            stock.getExchange(),
                            stock.getSymbol(),
                            stock.getName(),
                            snapshot.price(),
                            snapshot.percentageChange(),
                            logoService.getLogoUrl(stock.getSymbol(), logoSize)
                    );
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Transactional
    public MarketMoversDTO getTopMovers(int top, int logoSize) {
        MarketTopMovers marketMovers = alpacaFacade.getTopMarketMovers(top);

        List<StockMoverDTO> gainers = getMarketMovers(marketMovers.gainers(), logoSize);
        List<StockMoverDTO> losers = getMarketMovers(marketMovers.losers(), logoSize);


        return new MarketMoversDTO(gainers, losers);
    }

    private List<StockMoverDTO> getMarketMovers(List<MarketTopMover> marketMovers, int logoSize) {
        List<String> symbols = marketMovers.stream()
                .map(MarketTopMover::symbol)
                .toList();

        Map<String, Stock> stocksBySymbol = stockRepository
                .findAllBySymbolIn(symbols)
                .stream()
                .collect(Collectors.toMap(
                        Stock::getSymbol,
                        Function.identity()
                ));

        return marketMovers.stream()
                .map(mover -> {
                    Stock stock = stocksBySymbol.get(mover.symbol());

                    if (stock == null) {
                        log.warn("Symbol {} not found", mover.symbol());
                        return null;
                    }

                    return new StockMoverDTO(
                            stock.getId(),
                            stock.getExchange(),
                            stock.getSymbol(),
                            stock.getName(),
                            mover.price(),
                            mover.percentChange(),
                            mover.change(),
                            logoService.getLogoUrl(stock.getSymbol(), logoSize)
                    );
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public StockDTO findStockBySymbol(String symbol) {
        Stock stock = stockRepository.findStockBySymbol(symbol)
                .orElseThrow(() -> new StockNotFoundException(symbol));
        return stockMapper.toDTO(stock);
    }

    public boolean isActiveStockExistsBySymbol(String symbol) {
        return stockRepository.existsActiveStockBySymbol(symbol);
    }

    public List<StockSnapshotDTO> getSnapshots(List<String> symbols) {
        return getSnapshotsFromAlpaca(symbols);
    }

    private List<StockSnapshotDTO> getSnapshotsFromAlpaca(List<String> symbols) {

        List<String> allBySymbols = stockRepository.findAllSymbols(symbols);

        Map<String, MarketStockSnapshot> snapshots = alpacaFacade.getSnapshots(allBySymbols);

        return snapshots.values().stream()
                .map(snapshot -> {
                    BigDecimal currentPrice = snapshot.latestTrade().p();
                    BigDecimal previousClose = snapshot.prevDailyBar().close();

                    BigDecimal amountChange = currentPrice.subtract(previousClose);

                    BigDecimal percentageChange = amountChange
                            .divide(previousClose, 6, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));

                    return StockSnapshotDTO.builder()
                            .symbol(snapshot.symbol())
                            .price(snapshot.latestTrade().p())
                            .amountChange(amountChange)
                            .percentageChange(percentageChange)
                            .build();
                })
                .toList();
    }

    public StockPriceHistoryDTO getPriceHistory(String symbol) {

        if (!stockRepository.existsActiveStockBySymbol(symbol)) {
            throw new StockNotFoundException(symbol);
        }

        List<CandleDTO> candles = candleApi.getCandles(symbol, Timeframe.DAY_1);

        BigDecimal day = test(candles, symbol, 1, ChronoUnit.DAYS);
        BigDecimal week = test(candles, symbol, 1, ChronoUnit.WEEKS);
        BigDecimal month = test(candles, symbol, 1, ChronoUnit.MONTHS);
        BigDecimal quarter = test(candles, symbol, 3, ChronoUnit.MONTHS);
        BigDecimal year = test(candles, symbol, 1, ChronoUnit.YEARS);

        return StockPriceHistoryDTO.builder()
                .symbol(symbol)
                .day(day)
                .week(week)
                .month(month)
                .quarter(quarter)
                .year(year)
                .build();
    }

    public BigDecimal test(List<CandleDTO> candles, String symbol, int amount, ChronoUnit unit) {

        ZonedDateTime targetDate = marketClock.now()
                .atZone(ZoneOffset.UTC)
                .minus(amount, unit);

        Optional<CandleDTO> optionalCandle = candles.stream()
                .filter(candle -> !candle.timestamp().isAfter(targetDate.toInstant()))
                .max(Comparator.comparing(CandleDTO::timestamp));

        if (optionalCandle.isPresent()) {
            log.debug("Found candle for symbol {} (target {}): {}", symbol, targetDate, optionalCandle.get());

            return optionalCandle.get().close();
        } else {
            log.warn("No candle found for symbol {} before {}", symbol, targetDate);
            return BigDecimal.ZERO;
        }
    }
}