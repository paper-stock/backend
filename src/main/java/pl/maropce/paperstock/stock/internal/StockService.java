package pl.maropce.paperstock.stock.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.maropce.paperstock.alpaca.AlpacaFacade;
import pl.maropce.paperstock.alpaca.MarketStockSnapshot;
import pl.maropce.paperstock.alpaca.MarketTopMover;
import pl.maropce.paperstock.alpaca.MarketTopMovers;
import pl.maropce.paperstock.logodev.LogoService;
import pl.maropce.paperstock.stock.StockDTO;
import pl.maropce.paperstock.stock.internal.dto.*;
import pl.maropce.paperstock.stock.internal.exception.StockNotFoundException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private final StockSnapshotMapper stockSnapshotMapper;

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

    public List<StockSnapshotDTO> getMostActiveStocks(int top, int logoSize) {

        List<String> symbols = alpacaFacade.getMostActiveStocks(top);

        Map<String, Stock> stocksBySymbol = stockRepository
                .findAllBySymbolIn(symbols)
                .stream()
                .collect(Collectors.toMap(
                        Stock::getSymbol,
                        Function.identity()
                ));

        Map<String, MarketStockSnapshot> snapshots =
                alpacaFacade.getSnapshots(symbols);

        return symbols.stream()
                .map(symbol -> {
                    Stock stock = stocksBySymbol.get(symbol);
                    MarketStockSnapshot snapshot = snapshots.get(symbol);

                    if (stock == null || snapshot == null) {
                        log.warn("Symbol {} not found", symbol);
                        return null;
                    }

                    return stockSnapshotMapper.toStockSnapshotDTO(
                            stock,
                            snapshot,
                            logoService.getLogoUrl(symbol, logoSize)
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
}