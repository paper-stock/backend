package pl.maropce.paperstock.payment.internal.deposit;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.UUID;

public interface DepositRepository extends JpaRepository<Deposit, UUID> {

    /**
     * Locks the row until the transaction completes, preventing concurrent
     * webhooks for the same session from simultaneously passing the status check.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Deposit> findWithLockByStripeSessionId(String stripeSessionId);
}
