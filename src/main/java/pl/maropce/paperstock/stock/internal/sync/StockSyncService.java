package pl.maropce.paperstock.stock.internal.sync;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.maropce.paperstock.alpaca.AlpacaFacade;
import pl.maropce.paperstock.stock.internal.Stock;
import pl.maropce.paperstock.stock.internal.StockRepository;

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
    private final AlpacaFacade alpacaFacade;
    private final StockSyncMapper stockSyncMapper;

    @Transactional
    public void syncStocks() {
        log.info("Starting Alpaca assets synchronization");

        List<AssetData> assets = alpacaFacade.getAllAssets()
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
                created++;
            } else if (stock.updateFrom(providedStock)) {
                updated++;
            } else {
                unchanged++;
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
}
