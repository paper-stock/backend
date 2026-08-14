package pl.maropce.paperstock.logodev.internal;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "logo.dev")
public record LogoDevProperties(
        String publishableKey,
        String host
) {
}
