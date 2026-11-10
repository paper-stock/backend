package pl.maropce.paperstock.alpaca.ws.chart.client;

public enum SubscriptionCommand {
    SUBSCRIBE,
    UNSUBSCRIBE,
    UNKNOWN;

    public static SubscriptionCommand getCommandFromString(String command) {
        try {
            return SubscriptionCommand.valueOf(command.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
