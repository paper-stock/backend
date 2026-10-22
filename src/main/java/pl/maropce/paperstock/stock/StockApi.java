package pl.maropce.paperstock.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.maropce.paperstock.stock.internal.StockService;

@Component
@RequiredArgsConstructor
public class StockApi {

    private final StockService stockService;

    public StockDTO findStockBySymbol(String symbol) {
        return stockService.findStockBySymbol(symbol);
    }

    public boolean isActiveStockExists(String symbol) {
        return stockService.isActiveStockExistsBySymbol(symbol);
    }
}
