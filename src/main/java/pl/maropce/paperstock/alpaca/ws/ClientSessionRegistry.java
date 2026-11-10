package pl.maropce.paperstock.alpaca.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ClientSessionRegistry {

    private final Map<String, WebSocketSession> sessions =
            new ConcurrentHashMap<>();

    public void register(WebSocketSession session) {
        sessions.computeIfAbsent(
                session.getId(),
                _ -> new ConcurrentWebSocketSessionDecorator(
                        session,
                        30_000,
                        512 * 1024
                )
        );
    }

    public void unregister(String sessionId) {
        WebSocketSession session = sessions.remove(sessionId);

        if (session != null) {
            try {
                if (session.isOpen()) {
                    session.close(CloseStatus.SERVER_ERROR);
                }
            } catch (IOException e) {
                log.warn("Failed to close session {}", sessionId, e);
            }
        }
    }

    public WebSocketSession get(String sessionId) {
        return sessions.get(sessionId);
    }

    public Set<WebSocketSession> get(Set<String> sessionIds) {
        Set<WebSocketSession> result = new HashSet<>(sessionIds.size());
        for (String sessionId : sessionIds) {
            WebSocketSession session = sessions.get(sessionId);
            if (session != null) {
                result.add(session);
            }
        }
        return result;
    }

    public boolean hasSessions() {
        return !sessions.isEmpty();
    }

    public void sendMessage(String sessionId, String message) {
        WebSocketSession session = sessions.get(sessionId);

        if (session == null || !session.isOpen()) {
            return;
        }

        try {
            session.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            unregister(sessionId);
            log.warn("Failed to send message to session: {}", sessionId, e);
        }
    }

    public void sendMessage(Set<String> sessionIds, String message) {
        for (String sessionId : sessionIds) {
            sendMessage(sessionId, message);
        }
    }
}
