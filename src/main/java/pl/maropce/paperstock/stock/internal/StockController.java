package pl.maropce.paperstock.stock.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.maropce.paperstock.stock.StockDTO;
import pl.maropce.paperstock.stock.internal.dto.MarketMoversDTO;
import pl.maropce.paperstock.stock.internal.dto.StockSnapshotDTO;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
class StockController {

    private final StockService stockService;

    @GetMapping("/search")
    public List<StockDTO> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "64") int logoSize
    ) {
        return stockService.search(q, logoSize);
    }

    @GetMapping("/most-actives")
    public List<StockSnapshotDTO> getMostActiveStocks(
            @RequestParam(defaultValue = "10") int top,
            @RequestParam(defaultValue = "64") int logoSize
    ) {
        return stockService.getMostActiveStocks(top, logoSize);
    }

    @GetMapping("/top-movers")
    public MarketMoversDTO getTopMovers(
            @RequestParam(defaultValue = "10") int top,
            @RequestParam(defaultValue = "64") int logoSize
    ) {
        return stockService.getTopMovers(top, logoSize);
    }
}
