package pl.maropce.paperstock.alpaca.ws.chart.alpaca;

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
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.*;

@Component
@Slf4j
@NullMarked
class AlpacaWebSocketHandler extends TextWebSocketHandler {

    private final ClientSessionRegistry clientSessionRegistry;
    private final SymbolSubscriptionManager symbolSubscriptionManager;
    private final ObjectMapper objectMapper;

    AlpacaWebSocketHandler(
            @Qualifier("chartSessionRegistry") ClientSessionRegistry clientSessionRegistry,
            @Qualifier("chartSubscriptionManager") SymbolSubscriptionManager symbolSubscriptionManager,
            ObjectMapper objectMapper
    ) {
        this.clientSessionRegistry = clientSessionRegistry;
        this.symbolSubscriptionManager = symbolSubscriptionManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("AlpacaWebSocketHandler connection established");
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.debug("AlpacaWebSocketHandler received message: {}", message.getPayload());
        routeMessage(session, message.getPayload());
    }

    public void routeMessage(WebSocketSession session, String payload) {
        try {

            JsonNode root = objectMapper.readTree(payload);

            JsonNode first = root.get(0);
            String type = first.get("T").asString();

            switch (type) {
                case "t", "b" -> handleMarketData(root);
                case "success" -> handleSuccess(session, first);
                case "error" -> log.error("Alpaca stream error: {}", first);
                case "subscription" -> log.debug("Alpaca subscription state: {}", first);
                case null, default -> log.warn("Alpaca stream message of unknown type: {}", first);
            }

        } catch (Exception e) {
            log.error("Error routing message: {}", payload, e);
        }
    }

    private void handleSuccess(WebSocketSession session, JsonNode element) {
        JsonNode msgNode = element.get("msg");
        String msg = msgNode != null ? msgNode.asString() : null;

        if ("connected".equals(msg)) {
            log.info("Alpaca WebSocket connected successfully");
        } else if ("authenticated".equals(msg)) {
            log.info("Alpaca WebSocket authenticated successfully");
            resyncSubscriptions(session);
        }
    }

    private void handleMarketData(JsonNode root) {
        final Map<String, JsonNode> symbolTradeMap = new HashMap<>();

        for (JsonNode element : root) {
            final String type = element.get("T").asString();
            final String symbol = element.get("S").asString();
            switch (type) {
                case "t" -> {
                    List<String> conditions = new ArrayList<>();
                    element.get("c").forEach(node -> conditions.add(node.asString()));
                    if (conditions.contains("I")) continue;

                    symbolTradeMap.put(symbol, element);
                }
                case "b" -> sendMessageToSubscribers(symbol, element);
            }
        }

        symbolTradeMap.forEach(this::sendMessageToSubscribers);
    }

    private void sendMessageToSubscribers(String symbol, JsonNode tradeMessage) {
        Set<String> webSocketSessions = symbolSubscriptionManager.clientsFor(symbol);
        clientSessionRegistry.sendMessage(webSocketSessions, tradeMessage.toString());
    }

    private void resyncSubscriptions(WebSocketSession session) {
        Set<String> symbols = symbolSubscriptionManager.allSubscribedSymbols();
        if (symbols.isEmpty()) {
            return;
        }
        try {
            ObjectNode msg = objectMapper.createObjectNode()
                    .put("action", "subscribe");


            ArrayNode trades = msg.putArray("trades");
            ArrayNode bars = msg.putArray("bars");
            symbols.forEach(s -> {
                trades.add(s);
                bars.add(s);
            });
            session.sendMessage(new TextMessage(msg.toPrettyString()));
            log.info("Resynced {} symbol(s) after authentication: {}", symbols.size(), symbols);
        } catch (Exception e) {
            log.error("Failed to resync subscriptions after authentication", e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error(
                "WebSocket transport error, session={}",
                session.getId(),
                exception
        );
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        log.info("AlpacaWebSocketHandler connection closed with status={}", closeStatus);
        if (!symbolSubscriptionManager.allSubscribedSymbols().isEmpty()) {
            log.warn("AlpacaWebSocketHandler connection closed but there are still subscribed symbols: {}", symbolSubscriptionManager.allSubscribedSymbols());

        }
    }
}
