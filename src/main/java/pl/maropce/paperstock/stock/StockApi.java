package pl.maropce.paperstock.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.maropce.paperstock.stock.internal.StockService;
import pl.maropce.paperstock.stock.internal.exception.StockNotFoundException;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StockApi {

    private final StockService stockService;

    public Optional<StockDTO> findStockBySymbol(String symbol) {
        try {
            return Optional.of(stockService.findStockBySymbol(symbol));
        } catch (StockNotFoundException e) {
            return Optional.empty();
        }
    }
}
