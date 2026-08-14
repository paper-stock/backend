package pl.maropce.paperstock.security.internal.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;

@Configuration
@Slf4j
public class SecurityHeadersConfig {

    @Value("${app.security.frame-options:DENY}")
    private String frameOptions;

    @Bean
    public Customizer<HeadersConfigurer<HttpSecurity>> frameOptionsCustomizer() {
        return headers -> {
            if ("DENY".equalsIgnoreCase(frameOptions)) {
                log.info("Frame options set to DENY");
                headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::deny);

            } else if ("SAMEORIGIN".equalsIgnoreCase(frameOptions)) {
                log.warn("Frame options set to SAMEORIGIN (allows H2 console / internal frames - suitable for development).");
                headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin);

            } else {
                log.error("INVALID CONFIGURATION: Unsupported frame-options value: '{}'. " +
                        "Valid values are 'DENY' or 'SAMEORIGIN'. " +
                        "Falling back to 'DENY' to prevent clickjacking attacks.", frameOptions);
                headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::deny);
            }
        };
    }
}
