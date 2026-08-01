package pl.maropce.paperstock.alpaca.config;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class AlpacaClientConfig {

    @Bean
    @Qualifier("alpacaDataWebClient")
    WebClient alpacaDataWebClient(
            WebClient.Builder builder,
            AlpacaProperties properties
    ) {
        return builder
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .baseUrl(properties.dataUrl())
                .defaultHeader("APCA-API-KEY-ID", properties.key())
                .defaultHeader("APCA-API-SECRET-KEY", properties.secret())
                .build();
    }

    @Bean
    @Qualifier("alpacaTradingWebClient")
    WebClient alpacaTradingWebClient(
            WebClient.Builder builder,
            AlpacaProperties properties
    ) {
        return builder
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .baseUrl(properties.tradingUrl())
                .defaultHeader("APCA-API-KEY-ID", properties.key())
                .defaultHeader("APCA-API-SECRET-KEY", properties.secret())
                .build();
    }

    private HttpClient httpClient() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .responseTimeout(Duration.ofSeconds(5));
    }
}
