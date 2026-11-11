package pl.maropce.paperstock.alpaca.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.maropce.paperstock.alpaca.ws.ClientSessionRegistry;
import pl.maropce.paperstock.alpaca.ws.SymbolSubscriptionManager;
import pl.maropce.paperstock.alpaca.ws.config.WebSocketProperties;

@Configuration
public class WebSocketRegistryConfiguration {

    @Bean
    public WebSocketProperties webSocketProperties(WebSocketProperties webSocketProperties) {
        return webSocketProperties;
    }

    @Bean("chartSessionRegistry")
    public ClientSessionRegistry chartSessionRegistry(WebSocketProperties webSocketProperties) {
        return new ClientSessionRegistry(webSocketProperties);
    }

    @Bean("priceUpdatesSessionRegistry")
    public ClientSessionRegistry tradesSessionRegistry(WebSocketProperties webSocketProperties) {
        return new ClientSessionRegistry(webSocketProperties);
    }

    @Bean("chartSubscriptionManager")
    public SymbolSubscriptionManager chartSubscriptionManager() {
        return new SymbolSubscriptionManager();
    }

    @Bean("priceUpdatesSubscriptionManager")
    public SymbolSubscriptionManager priceUpdatesSubscriptionManager() {
        return new SymbolSubscriptionManager();
    }
}
