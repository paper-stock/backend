package pl.maropce.paperstock.payment.internal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "stripe")
public record StripeProperties(
        String apiKey,
        String webhookSecret,
        Duration connectionTimeout,
        Duration readTimeout,
        int maxNetworkRetries,
        SessionProperties session
) {

    public record SessionProperties(
            String successUrl,
            String cancelUrl,
            String currency,
            Duration expiration
    ) {
    }
}
