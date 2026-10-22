package pl.maropce.paperstock.alpaca.ws.alpaca;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlpacaMessageAction {
    SUBSCRIBE("subscribe"),
    UNSUBSCRIBE("unsubscribe");

    private final String action;
}
