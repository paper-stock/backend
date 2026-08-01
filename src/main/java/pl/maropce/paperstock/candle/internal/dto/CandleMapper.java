package pl.maropce.paperstock.candle.internal.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import pl.maropce.paperstock.candle.internal.Candle;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CandleMapper {

    CandleDTO toDto(Candle candle);
}
