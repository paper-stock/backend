package pl.maropce.paperstock.stock;

import lombok.Getter;

@Getter
public enum StockStatus {
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String value;

    StockStatus(String value) {
        this.value = value;
    }
}
