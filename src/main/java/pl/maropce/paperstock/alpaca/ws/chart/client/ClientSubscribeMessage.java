package pl.maropce.paperstock.alpaca.ws.chart.client;

public record ClientSubscribeMessage(
        String command,
        String symbol
) {
}
