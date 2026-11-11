package pl.maropce.paperstock.alpaca.internal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import pl.maropce.paperstock.alpaca.*;
import pl.maropce.paperstock.alpaca.response.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AlpacaMapper {

    @Mapping(source = "alpacaBarResponse.o", target = "open")
    @Mapping(source = "alpacaBarResponse.h", target = "high")
    @Mapping(source = "alpacaBarResponse.l", target = "low")
    @Mapping(source = "alpacaBarResponse.c", target = "close")
    @Mapping(source = "alpacaBarResponse.t", target = "timestamp")
    MarketBar toMarketBar(AlpacaBarResponse alpacaBarResponse);

    @Mapping(source = "alpacaLatestQuoteResponse.ap", target = "askPrice")
    @Mapping(source = "alpacaLatestQuoteResponse.bp", target = "bidPrice")
    @Mapping(source = "alpacaLatestQuoteResponse.t", target = "timestamp")
    MarketLatestQuote toMarketLatestQuote(AlpacaLatestQuoteResponse alpacaLatestQuoteResponse);

    MarketStockSnapshot toMarketStockSnapshot(String symbol, AlpacaStockSnapshotResponse alpacaStockSnapshotResponse);

    @Mapping(source = "assetClass", target = "stockClass")
    MarketStock toMarketStock(AlpacaAssetResponse alpacaAssetResponse);

    MarketTopMovers toMarketTopMovers(AlpacaTopMoversResponse alpacaTopMoversResponse);

    MarketQuote toMarketQuote(AlpacaQuoteResponse alpacaQuoteResponse);

}
