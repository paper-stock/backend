package pl.maropce.paperstock.order.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.maropce.paperstock.appUser.AppUserApi;
import pl.maropce.paperstock.order.internal.dto.CancelOrderRequest;
import pl.maropce.paperstock.order.internal.dto.OrderDTO;
import pl.maropce.paperstock.order.internal.dto.OrderMapper;
import pl.maropce.paperstock.order.internal.dto.PlaceOrderRequest;
import pl.maropce.paperstock.order.internal.exception.OrderCannotBeCancelledException;
import pl.maropce.paperstock.order.internal.exception.OrderNotFoundException;
import pl.maropce.paperstock.position.PositionApi;
import pl.maropce.paperstock.stock.StockApi;
import pl.maropce.paperstock.stock.StockDTO;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final StockApi stockApi;
    private final OrderValidator orderValidator;
    private final AppUserApi appUserApi;
    private final PositionApi positionApi;

    @Transactional
    public OrderDTO placeOrder(UUID appUserId, PlaceOrderRequest request) {

        StockDTO stockDTO = stockApi.findStockBySymbol(request.stockSymbol());

        log.info("Placing order for user {}: {}", appUserId, request);

        orderValidator.validateOrder(appUserId, request, stockDTO);

        if (request.orderSide() == OrderSide.BUY) {
            appUserApi.payFunds(appUserId, request.funds());
        }

        if (request.orderSide() == OrderSide.SELL) {
            positionApi.reserveShares(appUserId, stockDTO.id(), request.shares());
        }

        Order order = Order.builder()
                .appUserId(appUserId)
                .orderSide(request.orderSide())
                .orderType(request.orderType())
                .orderStatus(OrderStatus.PENDING)
                .stockId(stockDTO.id())
                .stockSymbol(stockDTO.symbol())
                .reservedFunds(request.funds())
                .reservedShares(request.shares())
                .limitPrice(request.limitPrice())
                .stopPrice(request.stopPrice())
                .build();

        return orderMapper.toDTO(orderRepository.save(order));
    }

    @Transactional
    public OrderDTO cancelOrder(UUID appUserId, CancelOrderRequest cancelOrderRequest) {
        Order order = orderRepository.findById(cancelOrderRequest.orderId())
                .orElseThrow(() -> new OrderNotFoundException(cancelOrderRequest.orderId()));

        if (!order.getAppUserId().equals(appUserId)) {
            throw new OrderNotFoundException(cancelOrderRequest.orderId());
        }

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new OrderCannotBeCancelledException(order.getOrderStatus());
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // Refund funds or release shares
        if (order.getOrderSide() == OrderSide.BUY && order.getReservedFunds() != null) {
            appUserApi.addFunds(appUserId, order.getReservedFunds());
        } else if (order.getOrderSide() == OrderSide.SELL && order.getReservedShares() != null) {
            positionApi.releaseReservedShares(appUserId, order.getStockId(), order.getReservedShares());
        }

        return orderMapper.toDTO(order);
    }

    public List<OrderDTO> getOrdersForUser(UUID appUserId, OrderStatus orderStatus) {
        List<Order> orders;

        if (orderStatus != null) {
            orders = orderRepository.findAllByAppUserIdAndOrderStatus(appUserId, orderStatus);
        } else {
            orders = orderRepository.findAllByAppUserId(appUserId);
        }

        return orders.stream()
                .map(orderMapper::toDTO)
                .toList();
    }
}
