package pl.maropce.paperstock.payment.internal.deposit;

import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.maropce.paperstock.payment.FundsDeposited;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DepositService {

    private final DepositRepository depositRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Deposit createPending(UUID userId, BigDecimal amount, String currency) {
        Deposit deposit = Deposit.pending(userId, amount, currency);
        return depositRepository.save(deposit);
    }

    @Transactional
    public void attachSession(UUID depositId, String sessionId) {
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new IllegalStateException("Deposit not found: " + depositId));
        deposit.attachSession(sessionId);
        depositRepository.save(deposit);
    }

    @Transactional
    public void discard(UUID depositId) {
        depositRepository.findById(depositId)
                .orElseThrow(() -> new IllegalArgumentException("Deposit not found: " + depositId));
        depositRepository.deleteById(depositId);
        log.info("Deposit {} discarded", depositId);
    }

    @Transactional
    public void handlePaid(Session session) {
        Deposit deposit = findLocked(session.getId());

        if (deposit.getStatus() == DepositStatus.COMPLETED) {
            log.info("Deposit {} already completed, ignoring duplicate event", deposit.getId());
            return;
        }

        if (!"paid".equals(session.getPaymentStatus())) {
            log.info("Deposit {} not paid yet (payment_status={}), waiting for async result",
                    deposit.getId(), session.getPaymentStatus());
            return;
        }

        if (deposit.getStatus() != DepositStatus.PENDING) {
            throw new IllegalStateException(
                    "Paid session %s for deposit %s in unexpected status %s"
                            .formatted(session.getId(), deposit.getId(), deposit.getStatus()));
        }

        verifySessionMatchesDeposit(deposit, session);

        deposit.complete(session.getPaymentIntent());
        log.info("Deposit {} completed ({} {})", deposit.getId(), deposit.getAmount(), deposit.getCurrency());

        eventPublisher.publishEvent(new FundsDeposited(
                deposit.getId(),
                deposit.getUserId(),
                deposit.getAmount(),
                deposit.getCurrency()
        ));
    }

    @Transactional
    public void handleAsyncPaymentFailed(String sessionId) {
        Deposit deposit = findLocked(sessionId);

        if (deposit.getStatus() == DepositStatus.PENDING) {
            deposit.fail();
            depositRepository.save(deposit);
            log.info("Deposit {} failed (async payment failed)", deposit.getId());
        } else {
            log.info("Deposit {} is {}, ignoring async_payment_failed", deposit.getId(), deposit.getStatus());
        }
    }

    @Transactional
    public void handleExpired(String sessionId) {
        Deposit deposit = findLocked(sessionId);

        if (deposit.getStatus() == DepositStatus.PENDING) {
            deposit.expire();
            depositRepository.save(deposit);
            log.info("Deposit {} expired", deposit.getId());
        } else {
            log.info("Deposit {} is {}, ignoring session expiry", deposit.getId(), deposit.getStatus());
        }
    }

    private Deposit findLocked(String sessionId) {
        // Missing deposit is an unexpected state. Throw an exception to trigger a 500 response
        // and let Stripe retry the webhook. The failure will be visible in the Stripe Dashboard.
        return depositRepository.findWithLockByStripeSessionId(sessionId)
                .orElseThrow(() -> new IllegalStateException(
                        "No deposit found for Stripe session " + sessionId));
    }

    private void verifySessionMatchesDeposit(Deposit deposit, Session session) {
        Long amountTotal = session.getAmountTotal();
        if (amountTotal == null) {
            throw new IllegalStateException("Session " + session.getId() + " has no amount_total");
        }

        BigDecimal paidAmount = BigDecimal.valueOf(amountTotal, 2); // grosze -> PLN
        boolean amountOk = paidAmount.compareTo(deposit.getAmount()) == 0;
        boolean currencyOk = deposit.getCurrency().equalsIgnoreCase(session.getCurrency());
        boolean userOk = deposit.getUserId().toString().equals(session.getClientReferenceId());

        if (!amountOk || !currencyOk || !userOk) {
            throw new IllegalStateException(
                    ("Session %s does not match deposit %s: amount %s/%s, currency %s/%s, user %s/%s")
                            .formatted(session.getId(), deposit.getId(),
                                    paidAmount, deposit.getAmount(),
                                    session.getCurrency(), deposit.getCurrency(), session.getClientReferenceId(), deposit.getUserId()));
        }
    }
}
