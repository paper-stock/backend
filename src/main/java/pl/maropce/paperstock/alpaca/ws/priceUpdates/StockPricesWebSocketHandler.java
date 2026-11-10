package pl.maropce.paperstock.alpaca.ws.priceUpdates;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pl.maropce.paperstock.alpaca.ws.ClientSessionRegistry;
import pl.maropce.paperstock.alpaca.ws.SymbolSubscriptionManager;
import pl.maropce.paperstock.alpaca.ws.chart.client.ClientSubscribeMessage;
import pl.maropce.paperstock.alpaca.ws.chart.client.SubscriptionCommand;
import pl.maropce.paperstock.stock.StockApi;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@Slf4j
@NullMarked
public class StockPricesWebSocketHandler extends TextWebSocketHandler {

    private final ClientSessionRegistry clientSessionRegistry;
    private final SymbolSubscriptionManager symbolSubscriptionManager;
    private final StockApi stockApi;
    private final ObjectMapper objectMapper;


    public StockPricesWebSocketHandler(
            @Qualifier("priceUpdatesSessionRegistry") ClientSessionRegistry clientSessionRegistry,
            @Qualifier("priceUpdatesSubscriptionManager") SymbolSubscriptionManager symbolSubscriptionManager,
            StockApi stockApi, ObjectMapper objectMapper
    ) {
        this.clientSessionRegistry = clientSessionRegistry;
        this.symbolSubscriptionManager = symbolSubscriptionManager;
        this.stockApi = stockApi;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        clientSessionRegistry.register(session);

        log.info("New client connected: {}", session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.debug("Received message from client {}: {}", session.getId(), message.getPayload());
        WebSocketSession clientSession = clientSessionRegistry.get(session.getId());

        if (clientSession == null) {
            log.warn("Client session not found to handle message: {}", session.getId());
            return;
        }
        handleMessage(clientSession, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

        log.info("Client disconnected: {}", session.getId());

        symbolSubscriptionManager.allSymbolsForClient(session.getId())
                .forEach(symbol -> symbolSubscriptionManager.unsubscribe(session.getId(), symbol));

        clientSessionRegistry.unregister(session.getId());

        if (!clientSessionRegistry.hasSessions()) {
            log.info("No client connected");
        }
    }

    public void handleMessage(WebSocketSession session, TextMessage message) {

        ClientSubscribeMessage msg;
        try {
            msg = objectMapper.readValue(message.getPayload(), ClientSubscribeMessage.class);
        } catch (JacksonException e) {
            log.warn("Failed to parse client message, closing session {}", session.getId());
            closeSession(session, CloseStatus.BAD_DATA);
            return;
        }

        if (!isMessageValid(msg)) {
            String error =
                    msg.command() == null || msg.command().isBlank()
                            ? "Missing command"
                            : "Missing symbol";
            sendError(session, error);
            return;
        }

        if (!isActiveStock(msg.symbol())) {
            sendError(session, "Invalid symbol: " + msg.symbol());
            return;
        }

        SubscriptionCommand command = SubscriptionCommand.getCommandFromString(msg.command());

        switch (command) {
            case SUBSCRIBE -> {
                log.info("Client {} subscribed to symbol {}", session.getId(), msg.symbol());
                symbolSubscriptionManager.subscribe(session.getId(), msg.symbol());
            }
            case UNSUBSCRIBE -> {
                log.info("Client {} unsubscribed from symbol {}", session.getId(), msg.symbol());
                symbolSubscriptionManager.unsubscribe(session.getId(), msg.symbol());
            }
            case UNKNOWN -> sendError(session, "Invalid command: " + msg.command());
        }

    }

    private boolean isActiveStock(String symbol) {
        return stockApi.isActiveStockExists(symbol);
    }

    private boolean isMessageValid(ClientSubscribeMessage msg) {
        return msg.command() != null && !msg.command().isBlank()
                && msg.symbol() != null && !msg.symbol().isBlank();
    }

    private void sendError(WebSocketSession session, String errorMsg) {

        String payload = objectMapper.createObjectNode()
                .put("error", errorMsg)
                .toString();

        clientSessionRegistry.sendMessage(session.getId(), payload);

    }

    private void closeSession(WebSocketSession session, CloseStatus status) {
        try {
            session.close(status);
        } catch (IOException e) {
            log.error("Failed to close client session {}", session.getId(), e);
        }
    }
}