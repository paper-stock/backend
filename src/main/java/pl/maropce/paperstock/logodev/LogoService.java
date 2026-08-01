package pl.maropce.paperstock.logodev;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
