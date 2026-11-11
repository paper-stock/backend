package pl.maropce.paperstock.logodev;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.maropce.paperstock.logodev.internal.LogoDevProperties;

@Service
@RequiredArgsConstructor
public class LogoService {

    private final LogoDevProperties properties;

    public String getLogoUrl(String symbol, int size) {
        return properties.host() + "/ticker/"
                + symbol
                + "?token="
                + properties.publishableKey()
                + "&size=" + size
                + "&format=png"
                + "&retina=true"
                + "&fallback=monogram";
    }
}
