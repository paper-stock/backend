package pl.maropce.paperstock.wallet.internal;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> findWithLockByAppUserId(UUID appUserId);

    Optional<Wallet> findByAppUserId(UUID appUserId);

    boolean existsByAppUserId(UUID appUserId);
}
