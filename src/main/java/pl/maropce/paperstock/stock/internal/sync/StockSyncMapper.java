package pl.maropce.paperstock.stock.internal.sync;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import pl.maropce.paperstock.alpaca.MarketStock;
import pl.maropce.paperstock.stock.StockStatus;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StockSyncMapper {

    @Mapping(source = "stockClass", target = "assetClass")
    AssetData toAssetData(MarketStock response);

    default StockStatus mapStockStatus(String status) {
        return status == null
                ? null
                : StockStatus.valueOf(status.toUpperCase());
    }
}