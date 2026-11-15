package pl.maropce.paperstock.payment.internal.webhook;

import com.stripe.StripeClient;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import pl.maropce.paperstock.payment.internal.config.StripeProperties;
import pl.maropce.paperstock.payment.internal.deposit.DepositService;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
class PaymentWebhookService {

    private static final String CHECKOUT_SESSION_COMPLETED = "checkout.session.completed";
    private static final String CHECKOUT_SESSION_ASYNC_PAYMENT_SUCCEEDED = "checkout.session.async_payment_succeeded";
    private static final String CHECKOUT_SESSION_ASYNC_PAYMENT_FAILED = "checkout.session.async_payment_failed";
    private static final String CHECKOUT_SESSION_EXPIRED = "checkout.session.expired";

    private final StripeProperties stripeProperties;
    private final DepositService depositService;
    private final StripeClient stripeClient;

    public void handleWebhook(String payload, String signature) {

        Event event;
        try {
            event = stripeClient.constructEvent(
                    payload,
                    signature,
                    stripeProperties.webhookSecret()
            );
        } catch (SignatureVerificationException e) {
            log.warn("Webhook signature verification failed", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Webhook signature verification failed");
        }

        log.info("Stripe event received: id={}, type={}", event.getId(), event.getType());

        Session session = extractSession(event);
        depositIdOf(session).ifPresent(depositId -> {
            switch (event.getType()) {
                case CHECKOUT_SESSION_COMPLETED,
                     CHECKOUT_SESSION_ASYNC_PAYMENT_SUCCEEDED -> depositService.handlePaid(depositId, session);

                case CHECKOUT_SESSION_ASYNC_PAYMENT_FAILED -> depositService.handleAsyncPaymentFailed(depositId);

                case CHECKOUT_SESSION_EXPIRED -> depositService.handleExpired(depositId);

                default -> log.debug("Unhandled event: {}", event.getType());
            }
        });


    }

    /**
     * Extracts a {@link Session} from the given Stripe event.
     * <p>
     * Attempts to deserialize the event's data object using the standard
     * deserialization mechanism. If deserialization fails due to an API version
     * mismatch, falls back to unsafe deserialization.
     *
     * @param event Stripe event containing the checkout session data
     * @return deserialized checkout session
     * @throws IllegalStateException if deserialization fails or the event
     *                               does not contain a {@link Session}
     */
    private Session extractSession(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

        StripeObject object = deserializer.getObject().orElseGet(() -> {
            try {
                return deserializer.deserializeUnsafe();
            } catch (EventDataObjectDeserializationException e) {
                throw new IllegalStateException(
                        "Cannot deserialize data object of event " + event.getId(), e);
            }
        });

        if (object instanceof Session session) {
            return session;
        }
        throw new IllegalStateException(
                "Event %s (%s) does not contain a checkout session".formatted(event.getId(), event.getType()));
    }

    private Optional<UUID> depositIdOf(Session session) {
        Map<String, String> metadata = session.getMetadata();
        String raw = metadata == null ? null : metadata.get(DepositService.DEPOSIT_ID);
        if (raw == null) {
            log.warn("Session {} has no depositId in metadata, ignoring", session.getId());
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(raw));
        } catch (IllegalArgumentException e) {
            log.warn("Session {} has malformed depositId '{}', ignoring", session.getId(), raw);
            return Optional.empty();
        }
    }
}
