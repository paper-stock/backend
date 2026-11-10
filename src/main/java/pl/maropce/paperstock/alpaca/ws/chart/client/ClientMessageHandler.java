package pl.maropce.paperstock.alpaca.ws.chart.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import pl.maropce.paperstock.alpaca.ws.ClientSessionRegistry;
import pl.maropce.paperstock.alpaca.ws.chart.alpaca.ProviderWebSocketClient;
import pl.maropce.paperstock.stock.StockApi;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@Slf4j
public class ClientMessageHandler {

    private final ProviderWebSocketClient providerWebSocketClient;
    private final ObjectMapper objectMapper;
    private final StockApi stockApi;
    private final ClientSessionRegistry clientSessionRegistry;

    public ClientMessageHandler(
            @Qualifier("chartSessionRegistry") ClientSessionRegistry clientSessionRegistry,
            ProviderWebSocketClient providerWebSocketClient,
            ObjectMapper objectMapper,
            StockApi stockApi
    ) {
        this.providerWebSocketClient = providerWebSocketClient;
        this.objectMapper = objectMapper;
        this.stockApi = stockApi;
        this.clientSessionRegistry = clientSessionRegistry;
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
                providerWebSocketClient.subscribe(session.getId(), msg.symbol());
            }
            case UNSUBSCRIBE -> {
                log.info("Client {} unsubscribed from symbol {}", session.getId(), msg.symbol());
                providerWebSocketClient.unsubscribe(session.getId(), msg.symbol());
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
