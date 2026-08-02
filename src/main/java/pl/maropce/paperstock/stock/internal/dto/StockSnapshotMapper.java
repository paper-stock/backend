package pl.maropce.paperstock.stock.internal.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.maropce.paperstock.alpaca.AlpacaStockSnapshot;
import pl.maropce.paperstock.stock.internal.Stock;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class StockSnapshotMapper {

    public StockSnapshotDTO toStockSnapshotDTO(
            Stock stock,
            AlpacaStockSnapshot snapshot,
            String logoUrl
    ) {
        BigDecimal price = snapshot.latestTrade().p();

        BigDecimal percentageChange = price
                .subtract(snapshot.dailyBar().c())
                .divide(snapshot.dailyBar().c(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        return new StockSnapshotDTO(
                stock.getId(),
                stock.getExchange(),
                stock.getSymbol(),
                stock.getName(),
                price,
                percentageChange,
                logoUrl
        );
    }
}