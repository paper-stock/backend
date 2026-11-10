package pl.maropce.paperstock.alpaca.ws.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.websocket")
public record WebSocketProperties(
        Duration sendTimeLimit,
        DataSize bufferSizeLimit
) {}