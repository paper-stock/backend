package pl.maropce.paperstock.payment.internal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.maropce.paperstock.appUserDetails.AppUserDetails;
import pl.maropce.paperstock.payment.internal.dto.CheckoutSessionDTO;
import pl.maropce.paperstock.payment.internal.dto.CreateCheckoutSessionRequest;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/checkout-session")
    public ResponseEntity<CheckoutSessionDTO> newCheckoutSession(
            @AuthenticationPrincipal AppUserDetails appUserDetails,
            @RequestBody @Valid CreateCheckoutSessionRequest request
    ) {
        return ResponseEntity.ok(paymentService.createCheckoutSession(appUserDetails, request));
    }
}
