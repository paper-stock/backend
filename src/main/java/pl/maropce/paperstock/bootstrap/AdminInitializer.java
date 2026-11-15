package pl.maropce.paperstock.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer {

    private final AdminBootstrapService adminBootstrapService;

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        adminBootstrapService.bootstrapAdminAccount();
    }
}
