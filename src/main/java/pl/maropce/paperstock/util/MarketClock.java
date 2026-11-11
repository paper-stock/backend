package pl.maropce.paperstock.util;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class MarketClock {

    private static final Duration DELAY = Duration.ofMinutes(15);

    public Instant now() {
        return Instant.now().minus(DELAY);
    }
}
