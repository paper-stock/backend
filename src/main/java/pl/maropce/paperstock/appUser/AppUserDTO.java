package pl.maropce.paperstock.appUser;

import java.math.BigDecimal;
import java.util.UUID;

public record AppUserDTO(
        UUID id,
        String displayName,
        BigDecimal funds
) {}
