package pl.maropce.paperstock.payment.internal.deposit;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Deposit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private UUID userId;

    @Column(nullable = false, updatable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, updatable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private DepositStatus status;

    @Column(unique = true)
    private String stripeSessionId;

    @Column(unique = true)
    private String stripePaymentIntentId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant completedAt;

    public static Deposit pending(UUID userId, BigDecimal amount, String currency) {
        return Deposit.builder()
                .userId(userId)
                .amount(amount)
                .currency(currency)
                .status(DepositStatus.PENDING)
                .build();
    }

    public void attachSession(String sessionId) {
        this.stripeSessionId = sessionId;
    }

    public void complete(String paymentIntentId) {
        this.status = DepositStatus.COMPLETED;
        this.stripePaymentIntentId = paymentIntentId;
        this.completedAt = Instant.now();
    }

    public void fail() {
        this.status = DepositStatus.FAILED;
    }

    public void expire() {
        this.status = DepositStatus.EXPIRED;
    }
}
