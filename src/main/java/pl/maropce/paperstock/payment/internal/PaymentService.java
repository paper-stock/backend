package pl.maropce.paperstock.payment.internal;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.maropce.paperstock.appUserDetails.AppUserDetails;
import pl.maropce.paperstock.payment.internal.config.StripeProperties;
import pl.maropce.paperstock.payment.internal.deposit.Deposit;
import pl.maropce.paperstock.payment.internal.deposit.DepositService;
import pl.maropce.paperstock.payment.internal.dto.CheckoutSessionDTO;
import pl.maropce.paperstock.payment.internal.dto.CreateCheckoutSessionRequest;

import java.time.Instant;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private static final String CURRENCY = "usd";

    private final StripeProperties stripeProperties;
    private final DepositService depositService;

    public CheckoutSessionDTO createCheckoutSession(AppUserDetails appUserDetails, CreateCheckoutSessionRequest request) {

        Deposit deposit = depositService.createPending(appUserDetails.getAppUserId(), request.amount(), CURRENCY);

        Map<String, String> metadata = Map.of(
                "depositId", deposit.getId().toString()
        );

        SessionCreateParams params = createSessionParams(appUserDetails, request, metadata);

        try {
            Session session = Session.create(params);

            depositService.attachSession(deposit.getId(), session.getId());
            log.info("Checkout session created: {}", session.getId());
            return new CheckoutSessionDTO(session.getUrl());
        } catch (StripeException e) {
            depositService.discard(deposit.getId());
            log.error("Error creating checkout session", e);
            throw new RuntimeException(e);
        }

    }

    private SessionCreateParams createSessionParams(AppUserDetails appUserDetails, CreateCheckoutSessionRequest request, Map<String, String> metadata) {
        return SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setClientReferenceId(appUserDetails.getAppUserId().toString())
                .setCustomerEmail(appUserDetails.getUsername())
                .setLocale(SessionCreateParams.Locale.AUTO)
                .setSuccessUrl(stripeProperties.session().successUrl())
                .setCancelUrl(stripeProperties.session().cancelUrl())
                .putAllMetadata(metadata)
                .setAdaptivePricing(SessionCreateParams.AdaptivePricing.builder()
                        .setEnabled(true)
                        .build()
                )
                .setExpiresAt(
                        Instant.now()
                                .plus(stripeProperties.session().expiration())
                                .getEpochSecond())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(CURRENCY)
                                                .setUnitAmount(request.amount().longValue() * 100)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Virtual Funds")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();
    }
}
