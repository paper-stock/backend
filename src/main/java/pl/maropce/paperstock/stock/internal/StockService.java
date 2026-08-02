package pl.maropce.paperstock.stock.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.maropce.paperstock.alpaca.AlpacaMarketDataClient;
import pl.maropce.paperstock.alpaca.AlpacaMostActiveStock;
import pl.maropce.paperstock.alpaca.AlpacaMostActiveStocksResponse;
import pl.maropce.paperstock.alpaca.AlpacaStockSnapshot;
import pl.maropce.paperstock.logodev.LogoService;
import pl.maropce.paperstock.stock.internal.dto.StockDTO;
import pl.maropce.paperstock.stock.internal.dto.StockMapper;
import pl.maropce.paperstock.stock.internal.dto.StockSnapshotDTO;
import pl.maropce.paperstock.stock.internal.dto.StockSnapshotMapper;

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

    private final AlpacaMarketDataClient alpacaMarketDataClient;
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
                .findBySymbolContainingIgnoreCaseOrNameContainingIgnoreCaseOrExchangeContainingIgnoreCase(
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
        AlpacaMostActiveStocksResponse mostActiveStocks =
                alpacaMarketDataClient.getMostActiveStocks(top);

        List<String> symbols = mostActiveStocks.mostActives()
                .stream()
                .map(AlpacaMostActiveStock::symbol)
                .toList();


        Map<String, AlpacaStockSnapshot> snapshots = alpacaMarketDataClient.getSnapshots(symbols);

        List<Stock> allBySymbolIn = stockRepository
                .findAllBySymbolIn(symbols);

        Map<String, Stock> stocksBySymbol = allBySymbolIn
                .stream()
                .collect(Collectors.toMap(
                        Stock::getSymbol,
                        Function.identity()
                ));

        return symbols.stream()
                .map(symbol -> {
                    Stock stock = stocksBySymbol.get(symbol);
                    AlpacaStockSnapshot snapshot = snapshots.get(symbol);

                    if (stock == null || snapshot == null) {
                        return null;
                    }

                    return stockSnapshotMapper.toStockSnapshotDTO(stock, snapshot, logoService.getLogoUrl(symbol, logoSize));
                })
                .filter(Objects::nonNull)
                .toList();
    }

}