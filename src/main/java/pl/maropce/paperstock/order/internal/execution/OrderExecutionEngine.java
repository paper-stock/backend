package pl.maropce.paperstock.order.internal.execution;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.maropce.paperstock.alpaca.AlpacaFacade;
import pl.maropce.paperstock.alpaca.MarketQuote;

import pl.maropce.paperstock.order.internal.Order;
import pl.maropce.paperstock.order.internal.OrderRepository;
import pl.maropce.paperstock.order.internal.OrderStatus;
import pl.maropce.paperstock.util.MarketClock;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderExecutionEngine {

    private final OrderRepository orderRepository;
    private final OrderExecutionService orderExecutionService;
    private final MarketClock marketClock;
    private final AlpacaFacade alpacaFacade;

    private static Instant lastWindowEnd;

    @Scheduled(fixedDelay = 10_000)
    public void executeOrders() {

        Instant now = marketClock.now();

        Instant end = now.truncatedTo(ChronoUnit.SECONDS);
        Instant start = lastWindowEnd != null
                ? lastWindowEnd
                : end.minus(Duration.ofSeconds(10));

        log.debug("Executing orders for window: {} - {}", start, end);
        List<Order> pendingOrders = orderRepository.findAllByOrderStatus(OrderStatus.PENDING);

        if (pendingOrders.isEmpty()) {
            log.debug("No pending orders found for execution.");
            lastWindowEnd = end;
            return;
        }

        Set<String> symbols = pendingOrders.stream()
                .map(Order::getStockSymbol)
                .collect(Collectors.toSet());


        Map<String, List<MarketQuote>> quotes = alpacaFacade.getQuotes(symbols, start, end);

        for (Order order : pendingOrders) {
            //MarketQuote mq = new MarketQuote(BigDecimal.valueOf(100.0 + new Random().nextDouble() * 10.34), 0L, BigDecimal.valueOf(100.0 + new Random().nextDouble() * 10.0), 0, marketClock.now().minus(1, java.time.temporal.ChronoUnit.MINUTES));
            List<MarketQuote> marketQuotes = quotes.getOrDefault(order.getStockSymbol(), List.of());
            try {
                orderExecutionService.tryExecuteOrder(order, marketQuotes);
            } catch (Exception e) {
                log.error("Error executing order {}", order.getId(), e);
            }

        }

        lastWindowEnd = end;
    }
}
