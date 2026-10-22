package pl.maropce.paperstock.alpaca.ws.alpaca;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import pl.maropce.paperstock.alpaca.ws.client.ClientSessionRegistry;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
class SymbolSubscriptionManager {

    private final Map<String, SymbolSubscription> subscriptions = new ConcurrentHashMap<>();
    private final ClientSessionRegistry clientSessionRegistry;

    SymbolSubscriptionManager(ClientSessionRegistry clientSessionRegistry) {
        this.clientSessionRegistry = clientSessionRegistry;
    }

    public boolean subscribe(String clientId, String symbol) {
        SymbolSubscription sub = subscriptions.computeIfAbsent(symbol, SymbolSubscription::new);
        boolean firstClient = !sub.hasClients();
        sub.addClient(clientId);
        return firstClient;
    }

    public boolean unsubscribe(String clientId, String symbol) {
        SymbolSubscription sub = subscriptions.get(symbol);
        if (sub == null) return false;
        sub.removeClient(clientId);
        if (!sub.hasClients()) {
            subscriptions.remove(symbol);
            return true;
        }
        return false;
    }

    public Set<String> clientsFor(String symbol) {
        SymbolSubscription sub = subscriptions.get(symbol);

        if (sub == null) return Collections.emptySet();

        return clientSessionRegistry.get(sub.clients())
                .stream()
                .map(WebSocketSession::getId)
                .collect(Collectors.toSet());
    }

    public Set<String> allSymbolsForClient(String clientSessionId) {
        Set<String> symbols = new HashSet<>();
        subscriptions.forEach((symbol, subscription) -> {
            if (subscription.clients().contains(clientSessionId)) {
                symbols.add(symbol);
            }
        });
        return symbols;
    }

    public Set<String> allSubscribedSymbols() {
        return Set.copyOf(subscriptions.keySet());
    }
}
