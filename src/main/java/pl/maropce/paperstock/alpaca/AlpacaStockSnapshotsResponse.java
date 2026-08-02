package pl.maropce.paperstock.alpaca;

import java.util.Map;

public record AlpacaStockSnapshotsResponse(
        Map<String, AlpacaStockSnapshot> stocks
) {
}