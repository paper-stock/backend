package pl.maropce.paperstock.order.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.maropce.paperstock.appUser.AppUserApi;
import pl.maropce.paperstock.appUser.AppUserDTO;
import pl.maropce.paperstock.order.internal.dto.PlaceOrderRequest;
import pl.maropce.paperstock.order.internal.exception.InactiveStockException;
import pl.maropce.paperstock.order.internal.exception.InsufficientFundsException;
import pl.maropce.paperstock.order.internal.exception.InvalidOrderException;
import pl.maropce.paperstock.order.internal.exception.StockNotAvailableForBuyException;
import pl.maropce.paperstock.position.PositionApi;
import pl.maropce.paperstock.position.PositionDTO;
import pl.maropce.paperstock.stock.StockDTO;
import pl.maropce.paperstock.stock.StockStatus;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderValidator {

    private final AppUserApi appUserApi;
    private final PositionApi positionApi;

    public void validateOrder(UUID appUserId, PlaceOrderRequest request, StockDTO stockDTO) {

        validateOrderType(request);

        if (stockDTO.status() == StockStatus.INACTIVE) {
            throw new InactiveStockException();
        }

        if (request.orderSide() == OrderSide.BUY) {
            if(request.funds() == null) {
                throw new InvalidOrderException("Funds must be provided for BUY order");
            }
            validateStockIsTradableForBuying(stockDTO);
            AppUserDTO appUserDTO = appUserApi.findAppUserById(appUserId);
            validateAppUserFunds(appUserDTO, request);
        }

        if (request.orderSide() == OrderSide.SELL) {
            if (request.shares() == null) {
                throw new InvalidOrderException("Shares must be provided for SELL order");
            }
           validateAppUserShares(appUserId, stockDTO.id(), request);
        }
    }

    private static void validateStockIsTradableForBuying(StockDTO stockDTO) {
        if (!stockDTO.tradable()) {
            throw new StockNotAvailableForBuyException();
        }
    }

    private void validateAppUserFunds(AppUserDTO appUserDTO, PlaceOrderRequest request) {

        if (appUserDTO.funds().compareTo(request.funds()) < 0) {
            throw new InsufficientFundsException(appUserDTO.id());
        }
    }

    private void validateAppUserShares(UUID appUserId, UUID stockId, PlaceOrderRequest request) {

        PositionDTO positionDTO = positionApi.getPosition(appUserId, stockId);

        if (positionDTO.shares().compareTo(request.shares()) < 0) {
            throw new InvalidOrderException("User does not have enough shares to sell");
        }
    }

    private static void validateOrderType(PlaceOrderRequest request) {
        switch (request.orderType()) {
            case MARKET -> {
                if (request.limitPrice() != null || request.stopPrice() != null) {
                    throw new InvalidOrderException("Limit price and stop price should not be provided for MARKET order");
                }
            }
            case LIMIT -> {
                if (request.limitPrice() == null) {
                    throw new InvalidOrderException("Limit price is required for LIMIT order");
                }
            }
            case STOP -> {
                if (request.stopPrice() == null) {
                    throw new InvalidOrderException("Stop price is required for STOP order");
                }
            }
            case STOP_LIMIT -> {
                if (request.limitPrice() == null || request.stopPrice() == null) {
                    throw new InvalidOrderException(
                            "Limit price and stop price are required for STOP LIMIT order"
                    );
                }
            }
        }
    }
}
