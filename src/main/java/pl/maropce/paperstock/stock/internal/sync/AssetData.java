package pl.maropce.paperstock.stock.internal.sync;

import lombok.Builder;
import pl.maropce.paperstock.stock.StockStatus;

import java.util.UUID;

@Builder
public record AssetData(

        UUID externalId,
        String assetClass,
        String exchange,
        String symbol,
        String name,
        StockStatus status,
        boolean tradable
) {
}

