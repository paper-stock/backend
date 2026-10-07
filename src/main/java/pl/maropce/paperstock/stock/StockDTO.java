package pl.maropce.paperstock.stock;

import lombok.Builder;

import java.util.UUID;

@Builder
public record StockDTO(
        UUID id,
        String stockClass,
        String exchange,
        String symbol,
        String name,
        StockStatus status,
        boolean tradable,

        String logoUrl

) {
    public StockDTO withLogoUrl(String logoUrl) {
        return new StockDTO(id, stockClass, exchange, symbol, name, status, tradable, logoUrl);
    }
}
