package pl.maropce.paperstock.alpaca.ws.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import pl.maropce.paperstock.alpaca.ws.chart.client.ClientWebSocketHandler;
import pl.maropce.paperstock.alpaca.ws.priceUpdates.StockPricesWebSocketHandler;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final ClientWebSocketHandler clientWebSocketHandler;
    private final StockPricesWebSocketHandler stockPricesWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(clientWebSocketHandler, "/ws/market")
                .setAllowedOrigins("*");

        registry.addHandler(stockPricesWebSocketHandler, "/ws/prices")
                .setAllowedOrigins("*");
    }
}