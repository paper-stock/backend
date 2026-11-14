package pl.maropce.paperstock.payment.internal.webhook;

import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.maropce.paperstock.payment.internal.config.StripeProperties;
import pl.maropce.paperstock.payment.internal.deposit.DepositService;

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

    public void handleWebhook(String payload, String signature)
            throws StripeException {

        Event event = Webhook.constructEvent(
                payload,
                signature,
                stripeProperties.webhookSecret()
        );

        log.info("Stripe event received: id={}, type={}", event.getId(), event.getType());

        switch (event.getType()) {
            case CHECKOUT_SESSION_COMPLETED,
                 CHECKOUT_SESSION_ASYNC_PAYMENT_SUCCEEDED -> depositService.handlePaid(extractSession(event));

            case CHECKOUT_SESSION_ASYNC_PAYMENT_FAILED ->
                    depositService.handleAsyncPaymentFailed(extractSession(event).getId());

            case CHECKOUT_SESSION_EXPIRED -> depositService.handleExpired(extractSession(event).getId());

            default -> log.debug("Unhandled event: {}", event.getType());
        }
    }

    private Session extractSession(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

        StripeObject object = deserializer.getObject().orElseGet(() -> {
            // wersja API webhooka różni się od wersji SDK
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
}
