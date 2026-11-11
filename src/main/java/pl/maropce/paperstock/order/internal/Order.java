package pl.maropce.paperstock.order.internal;

import jakarta.persistence.*;
import lombok.*;
import pl.maropce.paperstock.util.MarketClock;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private UUID appUserId;

    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private OrderSide orderSide;

    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(nullable = false, updatable = false)
    private UUID stockId;

    @Column(nullable = false, updatable = false)
    private String stockSymbol;

    @Column(precision = 19, scale = 2, updatable = false)
    private BigDecimal reservedFunds; //BUY

    @Column(precision = 19, scale = 2)
    private BigDecimal refundedFunds; //BUY

    @Column(precision = 19, scale = 9)
    private BigDecimal reservedShares; //SELL

    @Column(precision = 19, scale = 2)
    private BigDecimal limitPrice;

    @Column(precision = 19, scale = 2)
    private BigDecimal stopPrice;

    @Column(nullable = false, updatable = false)
    private Instant createdAtMarketTime;

    private Instant triggeredAt;

    @PrePersist
    public void onCreated() {
        MarketClock marketClock = new MarketClock();
        this.createdAtMarketTime = marketClock.now();
    }
}