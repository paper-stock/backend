package pl.maropce.paperstock.order.internal.execution;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.maropce.paperstock.alpaca.MarketQuote;
import pl.maropce.paperstock.appUser.AppUserApi;
import pl.maropce.paperstock.order.internal.Order;
import pl.maropce.paperstock.order.internal.OrderRepository;
import pl.maropce.paperstock.order.internal.OrderStatus;
import pl.maropce.paperstock.position.AddNewPositionRequest;
import pl.maropce.paperstock.position.PositionApi;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderExecutionService {

    private static final int SHARE_SCALE = 9;
    private static final int FUNDS_SCALE = 2;

    private final AppUserApi appUserApi;
    private final OrderRepository orderRepository;
    private final PositionApi positionApi;

    @Transactional
    public void tryExecuteOrder(Order order, List<MarketQuote> marketQuotes) {

        for (MarketQuote quote : marketQuotes) {
            if (isQuoteEligibleForExecution(order, quote)) {
                switch (order.getOrderSide()) {
                    case BUY -> fillBuyOrder(order, quote);
                    case SELL -> fillSellOrder(order, quote);
                }
                break;
            }
        }
    }

    private boolean isQuoteEligibleForExecution(Order order, MarketQuote quote) {
        log.debug("Checking order {} with quote {}", order.getId(), quote);
        if (quote.timestamp().isBefore(order.getCreatedAtMarketTime())) {
            return false;
        }

        if (order.getOrderStatus() != OrderStatus.PENDING
                && order.getTriggeredAt() != null
                && quote.timestamp().isBefore(order.getTriggeredAt())) {
            return false;
        }

        return switch (order.getOrderSide()) {
            case BUY -> isValidPrice(quote.askPrice(), order, quote);
            case SELL -> isValidPrice(quote.bidPrice(), order, quote);
        };
    }

    private boolean isValidPrice(
            BigDecimal price,
            Order order,
            MarketQuote quote
    ) {
        if (price == null || price.signum() <= 0) {
            log.warn("Ask / Bid price is not available or invalid for order {}: {}", order.getId(), quote);
            return false;
        }

        return true;
    }

    private void fillSellOrder(Order order, MarketQuote quote) {
        BigDecimal bid = quote.bidPrice();
        BigDecimal sharesToSell = order.getReservedShares();

        // Calculate the total revenue from selling the shares
        BigDecimal revenue = sharesToSell.multiply(bid).setScale(FUNDS_SCALE, RoundingMode.DOWN);

        // Update the order status and blocked funds
        order.setOrderStatus(OrderStatus.FILLED);
        order.setTriggeredAt(quote.timestamp());

        // Add the revenue to the user's portfolio
        appUserApi.addFunds(order.getAppUserId(), revenue);
        positionApi.takeFromReservedShares(order.getAppUserId(), order.getStockId(), sharesToSell);

        log.debug("Sold position for order {}: user={}, stock={}, quantity={}, revenue={}", order.getId(), order.getAppUserId(), order.getStockSymbol(), sharesToSell, revenue);

        orderRepository.save(order);
    }

    private void fillBuyOrder(Order order, MarketQuote quote) {
        BigDecimal ask = quote.askPrice();
        BigDecimal blocked = order.getReservedFunds();

        // Calculate the number of shares that can be bought with the blocked funds
        BigDecimal shares = blocked.divide(ask, SHARE_SCALE, RoundingMode.DOWN);

        // Calculate the total cost of buying the shares
        BigDecimal cost = shares.multiply(ask).setScale(FUNDS_SCALE, RoundingMode.CEILING);

        // Ensure that the cost does not exceed the blocked funds
        if (cost.compareTo(blocked) > 0) {
            throw new IllegalStateException("Calculated cost exceeds blocked funds for order " + order.getId());
        }

        BigDecimal refund = blocked.subtract(cost);

        // If there is any refund, add it back to the user's funds
        if (refund.signum() > 0) {
            appUserApi.addFunds(order.getAppUserId(), refund);
        }

        log.debug("Adding new position for order {}: user={}, stock={}, quantity={}", order.getId(), order.getAppUserId(), order.getStockSymbol(), shares);

        positionApi.addPosition(new AddNewPositionRequest(order.getAppUserId(), order.getStockId(), order.getStockSymbol(), shares));

        order.setOrderStatus(OrderStatus.FILLED);
        order.setRefundedFunds(refund);
        order.setTriggeredAt(quote.timestamp());

        orderRepository.save(order);

    }
}
