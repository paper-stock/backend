package pl.maropce.paperstock.wallet.internal;

import jakarta.persistence.*;
import lombok.*;
import pl.maropce.paperstock.wallet.internal.exception.InsufficientFundsException;
import pl.maropce.paperstock.wallet.internal.exception.InvalidAmountException;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Builder(access = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID appUserId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    public static Wallet create(UUID appUserId) {
        return Wallet.builder()
                .appUserId(appUserId)
                .balance(BigDecimal.ZERO)
                .build();
    }

    public void deposit(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidAmountException(amount);
        }

        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidAmountException(amount);
        }

        if (amount.compareTo(this.balance) > 0) {
            throw new InsufficientFundsException(amount, this.balance);
        }

        this.balance = this.balance.subtract(amount);
    }
}
