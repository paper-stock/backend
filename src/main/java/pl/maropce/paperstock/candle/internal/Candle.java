package pl.maropce.paperstock.candle.internal;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"symbol", "timestamp", "timeframe"}
        )
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class Candle {

    @Id
    @GeneratedValue
    private UUID id;

    private String symbol;

    @Enumerated(EnumType.STRING)
    private Timeframe timeframe;

    private Instant timestamp;

    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
}
