package pl.maropce.paperstock;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularityTests {

    private final ApplicationModules modules =
            ApplicationModules.of(PaperStockApplication.class);

    @Disabled("Disabled because project is not modularized yet")
    @Test
    void verifiesModuleStructure() {
        modules.verify();
    }
}
