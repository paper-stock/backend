package pl.maropce.paperstock.alpaca.ws;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SymbolSubscriptionManager {

    private final Map<String, SymbolSubscription> subscriptions = new ConcurrentHashMap<>();

    public boolean subscribe(String clientId, String symbol) {
        SymbolSubscription sub = subscriptions.computeIfAbsent(symbol, SymbolSubscription::new);
        boolean firstClient = !sub.hasClients();
        sub.addClient(clientId);
        return firstClient;
    }

    public boolean subscribe(String clientId, List<String> symbols) {
        boolean firstClient = false;
        for (String symbol : symbols) {
            SymbolSubscription sub = subscriptions.computeIfAbsent(symbol, SymbolSubscription::new);
            if (!sub.hasClients()) {
                firstClient = true;
            }
            sub.addClient(clientId);
        }
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

    public boolean unsubscribe(String clientId, List<String> symbols) {
        boolean removed = false;
        for (String symbol : symbols) {
            SymbolSubscription sub = subscriptions.get(symbol);
            if (sub == null) continue;
            sub.removeClient(clientId);
            if (!sub.hasClients()) {
                subscriptions.remove(symbol);
                removed = true;
            }
        }
        return removed;
    }

    public Set<String> clientsFor(String symbol) {
        SymbolSubscription sub = subscriptions.get(symbol);

        if (sub == null) return Collections.emptySet();

        return Set.copyOf(sub.clients());
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

    static class SymbolSubscription {

        private final String symbol;

        private final Set<String> clientsSessions =
                ConcurrentHashMap.newKeySet();

        public SymbolSubscription(String symbol) {
            this.symbol = symbol;
        }

        public String symbol() {
            return symbol;
        }

        public void addClient(String clientId) {
            clientsSessions.add(clientId);
        }

        public void removeClient(String clientId) {
            clientsSessions.remove(clientId);
        }

        public boolean hasClients() {
            return !clientsSessions.isEmpty();
        }

        public Set<String> clients() {
            return Set.copyOf(clientsSessions);
        }
    }
}
