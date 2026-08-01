package pl.maropce.paperstock.stock.internal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.maropce.paperstock.stock.internal.sync.AssetData;

import java.util.ArrayList;
import java.util.List;
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
    private String status;
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

    public List<String> getChanges(AssetData assetData) {

        List<String> changes = new ArrayList<>();

        if (!Objects.equals(name, assetData.name())) {
            changes.add(
                    "name: " + name + " -> " + assetData.name()
            );
        }

        if (!Objects.equals(exchange, assetData.exchange())) {
            changes.add(
                    "exchange: " + exchange + " -> " + assetData.exchange()
            );
        }

        if (!Objects.equals(symbol, assetData.symbol())) {
            changes.add(
                    "symbol: " + symbol + " -> " + assetData.symbol()
            );
        }

        return changes;
    }
}
