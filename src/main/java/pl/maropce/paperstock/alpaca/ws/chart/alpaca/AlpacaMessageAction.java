package pl.maropce.paperstock.alpaca.ws.chart.alpaca;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
enum AlpacaMessageAction {
    SUBSCRIBE("subscribe"),
    UNSUBSCRIBE("unsubscribe");

    private final String action;
}
