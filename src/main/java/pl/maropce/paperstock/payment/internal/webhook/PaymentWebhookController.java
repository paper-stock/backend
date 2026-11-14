package pl.maropce.paperstock.payment.internal.webhook;

import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for handling Stripe webhook events related to payments.
 * <br>Only the Stripe service should send requests to this endpoint
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
class PaymentWebhookController {

    private final PaymentWebhookService paymentWebhookService;

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature
    ) throws StripeException {

        paymentWebhookService.handleWebhook(payload, signature);

        return ResponseEntity.ok().build();
    }
}
