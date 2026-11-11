package pl.maropce.paperstock.logodev.internal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.maropce.paperstock.logodev.LogoService;

import java.net.URI;

@RestController
@RequestMapping("/api/logos")
public class LogoController {

    private final LogoService logoService;

    public LogoController(LogoService logoService) {
        this.logoService = logoService;
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<Void> getLogo(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "64") int size
    ) {
        URI logoUri = URI.create(logoService.getLogoUrl(symbol, size));

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(logoUri)
                .header("Cache-Control", "max-age=86400")
                .build();
    }
}
