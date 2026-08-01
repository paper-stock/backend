package pl.maropce.paperstock.stock.internal.sync;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AssetData(

        UUID externalId,
        String assetClass,
        String exchange,
        String symbol,
        String name,
        String status,
        boolean tradable
) {
}

