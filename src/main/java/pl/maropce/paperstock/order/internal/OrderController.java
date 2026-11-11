package pl.maropce.paperstock.order.internal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.maropce.paperstock.appUserDetails.AppUserDetails;
import pl.maropce.paperstock.order.internal.dto.CancelOrderRequest;
import pl.maropce.paperstock.order.internal.dto.OrderDTO;
import pl.maropce.paperstock.order.internal.dto.PlaceOrderRequest;
import pl.maropce.paperstock.order.internal.execution.OrderExecutionEngine;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderExecutionEngine orderExecutionEngine;

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getOrders(
            @AuthenticationPrincipal AppUserDetails appUserDetails,
            @RequestParam(required = false) OrderStatus status

    ) {
        return ResponseEntity.ok(orderService.getOrdersForUser(appUserDetails.getAppUserId(), status));
    }

    @PostMapping
    public ResponseEntity<OrderDTO> placeOrder(
            @AuthenticationPrincipal AppUserDetails appUserDetails,
            @RequestBody @Valid PlaceOrderRequest placeOrderRequest
    ) {
        OrderDTO orderDTO = orderService.placeOrder(appUserDetails.getAppUserId(), placeOrderRequest);
        return ResponseEntity.created(URI.create("/api/orders/" + orderDTO.id())).body(orderDTO);
    }

    @PostMapping("/cancel")
    public ResponseEntity<OrderDTO> cancelOrder(
            @AuthenticationPrincipal AppUserDetails appUserDetails,
            @RequestBody @Valid CancelOrderRequest cancelOrderRequest
    ) {
        OrderDTO orderDTO = orderService.cancelOrder(appUserDetails.getAppUserId(), cancelOrderRequest);
        return ResponseEntity.ok(orderDTO);
    }

    @PostMapping("/execute-orders")
    public void executeOrders() {
        orderExecutionEngine.executeOrders();
    }
}
