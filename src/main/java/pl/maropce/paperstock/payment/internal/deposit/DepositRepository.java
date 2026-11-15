package pl.maropce.paperstock.payment.internal.deposit;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.UUID;

public interface DepositRepository extends JpaRepository<Deposit, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Deposit> findWithLockById(UUID id);
}
