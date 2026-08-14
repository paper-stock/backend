package pl.maropce.paperstock.stock.internal.sync;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import pl.maropce.paperstock.alpaca.MarketStock;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StockSyncMapper {

    @Mapping(source = "stockClass", target = "assetClass")
    AssetData toAssetData(MarketStock response);
}