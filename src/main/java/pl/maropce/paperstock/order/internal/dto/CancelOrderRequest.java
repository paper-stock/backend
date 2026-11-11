package pl.maropce.paperstock.order.internal.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CancelOrderRequest(
        @NotNull UUID orderId
) {
}
