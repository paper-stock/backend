package pl.maropce.paperstock.alpaca.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.maropce.paperstock.alpaca.ws.ClientSessionRegistry;
import pl.maropce.paperstock.alpaca.ws.SymbolSubscriptionManager;

@Configuration
public class WebSocketRegistryConfiguration {

    @Bean("chartSessionRegistry")
    public ClientSessionRegistry chartSessionRegistry() {
        return new ClientSessionRegistry();
    }

    @Bean("priceUpdatesSessionRegistry")
    public ClientSessionRegistry tradesSessionRegistry() {
        return new ClientSessionRegistry();
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
