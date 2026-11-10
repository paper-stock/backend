package pl.maropce.paperstock.stock.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.maropce.paperstock.stock.StockDTO;
import pl.maropce.paperstock.stock.StockSnapshotDTO;
import pl.maropce.paperstock.stock.internal.dto.MarketMoversDTO;
import pl.maropce.paperstock.stock.internal.dto.StockMostActiveDTO;
import pl.maropce.paperstock.stock.internal.dto.StockPriceHistoryDTO;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
class StockController {

    private final StockService stockService;

    @GetMapping("/search")
    public ResponseEntity<List<StockDTO>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "64") int logoSize
    ) {
        return ResponseEntity.ok(stockService.search(q, logoSize));
    }

    @GetMapping("/price-history")
    public ResponseEntity<StockPriceHistoryDTO> getPriceHistory(
            @RequestParam String symbol
    ) {
        return ResponseEntity.ok(stockService.getPriceHistory(symbol));
    }

    @GetMapping("/snapshots")
    public ResponseEntity<List<StockSnapshotDTO>> getSnapshots(
            @RequestParam List<String> symbols
    ) {
        return ResponseEntity.ok(stockService.getSnapshots(symbols));
    }

    @GetMapping("/most-actives")
    public ResponseEntity<List<StockMostActiveDTO>> getMostActiveStocks(
            @RequestParam(defaultValue = "10") int top,
            @RequestParam(defaultValue = "64") int logoSize
    ) {
        return ResponseEntity.ok(stockService.getMostActiveStocks(top, logoSize));
    }

    @GetMapping("/top-movers")
    public ResponseEntity<MarketMoversDTO> getTopMovers(
            @RequestParam(defaultValue = "10") int top,
            @RequestParam(defaultValue = "64") int logoSize
    ) {
        return ResponseEntity.ok(stockService.getTopMovers(top, logoSize));
    }
}
