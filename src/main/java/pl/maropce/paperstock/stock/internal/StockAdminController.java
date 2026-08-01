package pl.maropce.paperstock.stock.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.maropce.paperstock.stock.internal.sync.StockSyncService;

@RestController
@RequestMapping("/api/admin/stocks")
@RequiredArgsConstructor
public class StockAdminController {

    private final StockSyncService stockSyncService;

    @PostMapping("/sync")
    public void syncStocks() {
        stockSyncService.syncStocks();
    }
}
