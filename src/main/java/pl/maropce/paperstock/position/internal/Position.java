package pl.maropce.paperstock.position.internal;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Represents a user's ownership of a specific stock.
 *
 * <p>A position tracks both available and reserved shares.
 * The total number of owned shares is the sum of {@code shares}
 * and {@code reservedShares}.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID appUserId;

    @Column(nullable = false)
    private UUID stockId;

    @Column(nullable = false)
    private String stockSymbol;

    /**
     * Shares owned by the user and currently available for trading.
     *
     * <p>This value is updated when shares are bought, sold, reserved or released.
     * It excludes shares reserved for pending orders.
     *
     * <p>0 means the user has no freely available shares, but may still own
     * shares that are reserved for pending orders.
     */
    @Column(precision = 19, scale = 9, nullable = false)
    private BigDecimal shares;

    /**
     * Shares owned by the user that are reserved for pending orders.
     *
     * <p>This value is updated when an order is placed, filled or canceled.
     * Reserved shares are still owned by the user but cannot be used
     * for other orders until they are released or the order is filled.
     *
     * <p>0 means the user has no reserved shares.
     */
    @Column(precision = 19, scale = 9, nullable = false)
    private BigDecimal reservedShares;
}
