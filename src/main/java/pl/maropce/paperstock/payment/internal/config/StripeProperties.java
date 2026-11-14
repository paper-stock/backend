package pl.maropce.paperstock.payment.internal.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "stripe")
public record StripeProperties(
        String apiKey,
        String webhookSecret,
        SessionProperties session
) {

    public record SessionProperties(
            String successUrl,
            String cancelUrl,
            Duration expiration
    ) {
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }
}
