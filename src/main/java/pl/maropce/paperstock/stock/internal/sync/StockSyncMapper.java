package pl.maropce.paperstock.stock.internal.sync;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import pl.maropce.paperstock.alpaca.AlpacaAssetResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StockSyncMapper {

    @Mapping(target = "externalId", source = "id")
    AssetData toAssetData(AlpacaAssetResponse response);
}