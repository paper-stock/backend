package pl.maropce.paperstock.payment.internal;

import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
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
import pl.maropce.paperstock.payment.internal.exception.PaymentProviderException;

import java.time.Instant;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final StripeProperties stripeProperties;
    private final DepositService depositService;
    private final StripeClient stripeClient;

    public CheckoutSessionDTO createCheckoutSession(AppUserDetails appUserDetails, CreateCheckoutSessionRequest request) {

        Deposit deposit = depositService.createPending(appUserDetails.getAppUserId(), request.amount(), stripeProperties.session().currency());

        Map<String, String> metadata = Map.of(
                DepositService.DEPOSIT_ID, deposit.getId().toString()
        );

        SessionCreateParams params = createSessionParams(appUserDetails, request, metadata);

        RequestOptions requestOptions = RequestOptions.builder()
                .setIdempotencyKey(deposit.getId().toString())
                .build();

        try {
            Session session = stripeClient.v1().checkout().sessions().create(params, requestOptions);
            depositService.attachSession(deposit.getId(), session.getId());
            log.info("Checkout session created: {}", session.getId());
            return new CheckoutSessionDTO(session.getUrl());
        } catch (StripeException e) {
            log.error("Error creating checkout session", e);
            depositService.discard(deposit.getId());
            throw new PaymentProviderException();
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
                                                .setCurrency(stripeProperties.session().currency())
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
