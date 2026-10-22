package pl.maropce.paperstock.alpaca.ws.client;

public record ClientSubscribeMessage(
        String command,
        String symbol
) {
}
