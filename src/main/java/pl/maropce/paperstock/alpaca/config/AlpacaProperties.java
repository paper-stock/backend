package pl.maropce.paperstock.alpaca.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "alpaca.api")
public record AlpacaProperties(
        String key,
        String secret,
        String dataUrl,
        String tradingUrl,
        String streamUrl
) {
}
