package pl.maropce.paperstock.stock.internal.sync;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.maropce.paperstock.alpaca.AlpacaTradingClient;
import pl.maropce.paperstock.stock.internal.Stock;
import pl.maropce.paperstock.stock.internal.StockRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockSyncService {

    private final StockRepository stockRepository;
    private final AlpacaTradingClient alpacaTradingClient;
    private final StockSyncMapper stockSyncMapper;

    @Transactional
    public void syncStocks() {
        log.info("Starting Alpaca assets synchronization");

        List<AssetData> assets = alpacaTradingClient.getAllAssets()
                .stream()
                .map(stockSyncMapper::toAssetData)
                .toList();

        Map<UUID, Stock> existing = stockRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        Stock::getExternalId,
                        Function.identity()
                ));

        int created = 0;
        int updated = 0;
        int unchanged = 0;

        List<Stock> stocks = new ArrayList<>(assets.size());

        for (AssetData providedStock : assets) {

            Stock stock = existing.get(providedStock.externalId());

            if (stock == null) {
                stock = new Stock();
                stock.updateFrom(providedStock);
                logChange("""
                        [C] New stock created:
                        id: %s
                        symbol: %s
                        name: %s
                        exchange: %s
                        status: %s
                        assetClass: %s
                        tradable: %s
                        
                        """.formatted(
                        providedStock.externalId(),
                        providedStock.symbol(),
                        providedStock.name(),
                        providedStock.exchange(),
                        providedStock.status(),
                        providedStock.assetClass(),
                        providedStock.tradable()
                ));

                created++;
            } else {
                List<String> changes = stock.getChanges(providedStock);
                if (!changes.isEmpty()) {
                    logChange("""
                            Stock updated:
                            id: %s
                            symbol: %s
                            name: %s
                            exchange: %s
                            status: %s
                            assetClass: %s
                            tradable: %s
                            
                            Changes:
                            %s
                            
                            """.formatted(
                            providedStock.externalId(),
                            providedStock.symbol(),
                            providedStock.name(),
                            providedStock.exchange(),
                            providedStock.status(),
                            providedStock.assetClass(),
                            providedStock.tradable(),
                            String.join("\n", changes)
                    ));

                    stock.updateFrom(providedStock);
                    updated++;
                } else {
                    unchanged++;
                }
            }
            stocks.add(stock);
        }
        stockRepository.saveAll(stocks);
        log.info(
                "Assets synchronization finished. Total: {}, Created: {}, Updated: {}, Unchanged: {}",
                assets.size(),
                created,
                updated,
                unchanged
        );
    }

    private static final Path FILE = Paths.get("./stock-changes.log");

    public void logChange(String message) {
        try {
            Files.writeString(
                    FILE,
                    message + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new RuntimeException("Cannot write stock change log", e);
        }
    }
}
