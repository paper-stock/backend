package pl.maropce.paperstock.alpaca.ws.alpaca;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import pl.maropce.paperstock.alpaca.config.AlpacaProperties;

import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProviderWebSocketClient {

    private final SymbolSubscriptionManager subscriptionManager;
    WebSocketContainer container = ContainerProvider.getWebSocketContainer();

    private StandardWebSocketClient webSocketClient;
    private volatile WebSocketSession alpacaSession;

    private final AlpacaProperties alpacaProperties;
    private final AlpacaWebSocketHandler alpacaWebSocketHandler;

    private final AtomicBoolean connecting = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        container.setDefaultMaxTextMessageBufferSize(512 * 1024); // 0.5 MB
        webSocketClient = new StandardWebSocketClient(container);

    }

    private void ensureConnected() {
        if (isConnected()) {
            return;
        }

        if (connecting.get()) {
            log.debug("Connection already in progress, skipping...");
            return;
        }

        connect();
    }

    private void connect() {
        if (!connecting.compareAndSet(false, true)) {
            return;
        }

        log.info("Connecting to Alpaca WebSocket {}", alpacaProperties.streamUrl());
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("APCA-API-KEY-ID", alpacaProperties.key());
        headers.add("APCA-API-SECRET-KEY", alpacaProperties.secret());
        try {
            webSocketClient.execute(alpacaWebSocketHandler, headers, URI.create(alpacaProperties.streamUrl()))
                    .whenComplete((session, ex) -> {
                        try {
                            if (ex != null) {
                                log.error("Failed to establish connection with Alpaca WebSocket stream", ex);
                                return;
                            }

                            this.alpacaSession = session;
                            log.info("Alpaca WebSocket connected, remote={}", session.getRemoteAddress());
                        } finally {
                            connecting.set(false);
                        }
                    });
        } catch (Exception e) {
            log.error("Failed to establish connection with Alpaca WebSocket", e);
            connecting.set(false);
        }
    }

    private boolean isConnected() {

        WebSocketSession current = alpacaSession;

        return current != null
                && current.isOpen();
    }

    public void disconnect() {

        if (!isConnected()) {
            return;
        }

        WebSocketSession sessionToClose = this.alpacaSession;
        try {
            sessionToClose.close();
        } catch (IOException e) {
            log.error("Failed to close Alpaca WebSocket stream", e);
        } finally {
            // ensure the session did not change while disconnecting.
            if (this.alpacaSession == sessionToClose) {
                this.alpacaSession = null;
            }
        }
    }

    public void subscribe(String clientId, String symbol) {
        ensureConnected();

        if (!subscriptionManager.subscribe(clientId, symbol)) {
            return;
        }

        sendMessage(AlpacaMessageAction.SUBSCRIBE, symbol);
    }

    public void unsubscribe(String clientId, String symbol) {

        if (!subscriptionManager.unsubscribe(clientId, symbol)) {
            return;
        }

        sendMessage(AlpacaMessageAction.UNSUBSCRIBE, symbol);
    }

    public void sendMessage(AlpacaMessageAction action, String symbol) {
        String actionName = action.getAction();

        if (isConnected()) {
            try {
                String subscribeMessage = String.format("""
                        {
                            "action": "%s",
                            "trades": ["%s"],
                            "bars": ["%s"]
                        }
                        """, actionName, symbol, symbol);
                alpacaSession.sendMessage(new TextMessage(subscribeMessage));
                log.info("{}d to symbol: {}", actionName, symbol);
            } catch (Exception e) {
                log.error("Failed to send {} message for symbol: {}", actionName, symbol, e);
            }
        } else {
            log.debug("Cannot {} to symbol {} because the WebSocket session is not open.", actionName, symbol);
        }
    }

    public void unsubscribeAll(String clientSessionId) {

        Set<String> symbols = subscriptionManager.allSymbolsForClient(clientSessionId);
        for (String symbol : symbols) {
            unsubscribe(clientSessionId, symbol);
        }
    }

    @PreDestroy
    public void cleanup() {
        disconnect();
    }
}
