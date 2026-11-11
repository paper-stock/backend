package pl.maropce.paperstock.candle.internal.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import pl.maropce.paperstock.alpaca.MarketBar;
import pl.maropce.paperstock.candle.CandleDTO;
import pl.maropce.paperstock.candle.Timeframe;
import pl.maropce.paperstock.candle.internal.Candle;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CandleMapper {

    CandleDTO toDTO(Candle candle);

    @Mapping(target = "id", ignore = true)
    Candle toEntity(MarketBar marketBar, String symbol, Timeframe timeframe);
}
