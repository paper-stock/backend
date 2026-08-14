package pl.maropce.paperstock.candle.internal.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import pl.maropce.paperstock.alpaca.MarketBar;
import pl.maropce.paperstock.candle.internal.Candle;
import pl.maropce.paperstock.candle.internal.Timeframe;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CandleMapper {

    CandleDTO toDto(Candle candle);

    Candle toEntity(MarketBar marketBar, String symbol, Timeframe timeframe);
}
