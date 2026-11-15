package pl.maropce.paperstock.payment.internal.config;

import com.stripe.StripeClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class StripeConfig {

    @Bean
    StripeClient stripeClient(StripeProperties properties) {
        return StripeClient.builder()
                .setApiKey(properties.apiKey())
                .setConnectTimeout(Math.toIntExact(properties.connectionTimeout().toMillis()))
                .setReadTimeout(Math.toIntExact(properties.readTimeout().toMillis()))
                .setMaxNetworkRetries(properties.maxNetworkRetries())
                .build();
    }
}