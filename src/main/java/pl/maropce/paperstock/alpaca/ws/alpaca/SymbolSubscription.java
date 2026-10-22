package pl.maropce.paperstock.alpaca.ws.alpaca;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SymbolSubscription {

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
