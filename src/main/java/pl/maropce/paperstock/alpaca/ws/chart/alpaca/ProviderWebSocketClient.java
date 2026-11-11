package pl.maropce.paperstock.alpaca.ws.chart.alpaca;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import pl.maropce.paperstock.alpaca.config.AlpacaProperties;
import pl.maropce.paperstock.alpaca.ws.SymbolSubscriptionManager;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class ProviderWebSocketClient {

    private final SymbolSubscriptionManager subscriptionManager;
    private final AlpacaProperties alpacaProperties;
    private final AlpacaWebSocketHandler alpacaWebSocketHandler;

    private final WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    private final ObjectMapper objectMapper;

    private StandardWebSocketClient webSocketClient;
    private volatile WebSocketSession alpacaSession;

    private final AtomicBoolean connecting = new AtomicBoolean(false);

    ProviderWebSocketClient(
            @Qualifier("chartSubscriptionManager") SymbolSubscriptionManager subscriptionManager,
            AlpacaProperties alpacaProperties,
            AlpacaWebSocketHandler alpacaWebSocketHandler,
            ObjectMapper objectMapper) {
        this.subscriptionManager = subscriptionManager;
        this.alpacaProperties = alpacaProperties;
        this.alpacaWebSocketHandler = alpacaWebSocketHandler;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    private void init() {
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

    private void disconnect() {

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

    public void subscribe(String clientId, List<String> symbols) {
        ensureConnected();

        if (!subscriptionManager.subscribe(clientId, symbols)) {
            return;
        }

        sendMessage(AlpacaMessageAction.SUBSCRIBE, symbols);

    }

    public void unsubscribe(String clientId, List<String> symbols) {

        if (!subscriptionManager.unsubscribe(clientId, symbols)) {
            return;
        }

        sendMessage(AlpacaMessageAction.UNSUBSCRIBE, symbols);
    }

    private void sendMessage(AlpacaMessageAction action, List<String> symbols) {
        String actionName = action.getAction();
        String jsonSymbols = objectMapper.writeValueAsString(symbols);

        if (isConnected()) {
            try {
                String subscribeMessage = String.format("""
                        {
                            "action": "%s",
                            "trades": %s,
                            "bars": %s
                        }
                        """, actionName, jsonSymbols, jsonSymbols);
                alpacaSession.sendMessage(new TextMessage(subscribeMessage));
                log.info("{}d to symbols: {}", actionName, symbols);
            } catch (Exception e) {
                log.error("Failed to send {} message for symbols: {}", actionName, symbols, e);
            }
        } else {
            log.debug("Cannot {} to symbols {} because the WebSocket session is not open.", actionName, symbols);
        }
    }

    public void unsubscribeAll(String clientSessionId) {

        Set<String> symbols = subscriptionManager.allSymbolsForClient(clientSessionId);

        unsubscribe(clientSessionId, new ArrayList<>(symbols));
    }

    @PreDestroy
    public void cleanup() {
        disconnect();
    }
}
