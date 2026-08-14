package pl.maropce.paperstock.stock.internal.dto;

import java.util.List;

public record MarketMoversDTO(
        List<StockMoverDTO> gainers,
        List<StockMoverDTO> losers
) {
}
