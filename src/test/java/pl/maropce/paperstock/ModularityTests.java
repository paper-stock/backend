package pl.maropce.paperstock;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTests {

    private final ApplicationModules modules =
            ApplicationModules.of(PaperStockApplication.class);

    @Test
    void verifiesModuleStructure() {
        modules.verify();
    }
}
