package pl.maropce.paperstock.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bootstrap.admin")
public record AdminBootstrapProperties(
        String email,
        String initialPassword
) {
}
