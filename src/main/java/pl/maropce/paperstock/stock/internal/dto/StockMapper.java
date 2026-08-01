package pl.maropce.paperstock.stock.internal.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import pl.maropce.paperstock.stock.internal.Stock;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StockMapper {

    @Mapping(target = "logoUrl", ignore = true)
    StockDTO toDTO(Stock stock);
}
