package pl.maropce.paperstock.order.internal.dto;

import pl.maropce.paperstock.order.internal.OrderSide;
import pl.maropce.paperstock.order.internal.OrderStatus;
import pl.maropce.paperstock.order.internal.OrderType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderDTO(
        UUID id,
        UUID appUserId,

        OrderSide orderSide,
        OrderType orderType,
        OrderStatus orderStatus,

        UUID stockId,
        String stockSymbol,

        BigDecimal reservedFunds,
        BigDecimal refundedFunds,

        BigDecimal reservedShares,

        BigDecimal limitPrice,
        BigDecimal stopPrice,

        Instant createdAtMarketTime,
        Instant triggeredAt
) {
}
