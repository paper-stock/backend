package pl.maropce.paperstock.alpaca.ws.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pl.maropce.paperstock.alpaca.ws.alpaca.ProviderWebSocketClient;


@Component
@Slf4j
@RequiredArgsConstructor
@NullMarked
public class ClientWebSocketHandler extends TextWebSocketHandler {

    private final ClientSessionRegistry clientSessionRegistry;
    private final ClientMessageHandler clientMessageHandler;
    private final ProviderWebSocketClient providerWebSocketClient;

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
        clientMessageHandler.handleMessage(clientSession, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

        log.info("Client disconnected: {}", session.getId());

        providerWebSocketClient.unsubscribeAll(session.getId());
        clientSessionRegistry.unregister(session.getId());

        if (!clientSessionRegistry.hasSessions()) {
            log.info("No client connected");
        }
    }
}