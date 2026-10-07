package pl.maropce.paperstock.stock.internal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.maropce.paperstock.stock.StockStatus;
import pl.maropce.paperstock.stock.internal.sync.AssetData;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private UUID externalId;

    @Column(nullable = false)
    private String symbol;

    private String stockClass;
    private String exchange;
    private String name;

    @Enumerated(EnumType.STRING)
    private StockStatus status;

    private boolean tradable;

    public boolean updateFrom(AssetData assetData) {
        boolean changed = hasChanges(assetData);

        this.externalId = assetData.externalId();
        this.symbol = assetData.symbol();
        this.stockClass = assetData.assetClass();
        this.exchange = assetData.exchange();
        this.name = assetData.name();
        this.status = assetData.status();
        this.tradable = assetData.tradable();

        return changed;
    }

    private boolean hasChanges(AssetData assetData) {
        return !Objects.equals(externalId, assetData.externalId())
                || !Objects.equals(symbol, assetData.symbol())
                || !Objects.equals(stockClass, assetData.assetClass())
                || !Objects.equals(exchange, assetData.exchange())
                || !Objects.equals(name, assetData.name())
                || !Objects.equals(status, assetData.status())
                || tradable != assetData.tradable();
    }
}
