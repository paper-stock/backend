package pl.maropce.paperstock.alpaca;

import java.util.UUID;

public record MarketStock(
        UUID externalId,
        String stockClass,
        String exchange,
        String symbol,
        String name,
        String status,
        boolean tradable
) {
}
