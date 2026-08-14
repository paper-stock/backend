package pl.maropce.paperstock.stock.internal.sync;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
class StockSyncScheduler {

    private final StockSyncService stockSyncService;

    @Scheduled(
            cron = "${stock.sync.cron}",
            zone = "${stock.sync.timezone}"
    )
    public void syncAssets() {
        stockSyncService.syncStocks();
    }
}