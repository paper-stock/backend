package pl.maropce.paperstock.alpaca.config;

import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.ReactorClientHttpRequestFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import pl.maropce.paperstock.alpaca.exception.AlpacaApiException;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Configuration
@Slf4j
class AlpacaClientConfig {

    @Bean
    @Qualifier("alpacaDataRestClient")
    public RestClient alpacaDataRestClient(AlpacaProperties properties) {
        return RestClient.builder()
                .requestFactory(new ReactorClientHttpRequestFactory(httpClient()))
                .baseUrl(properties.dataUrl())
                .defaultHeader("APCA-API-KEY-ID", properties.key())
                .defaultHeader("APCA-API-SECRET-KEY", properties.secret())
                .requestInterceptor(new LoggingInterceptor())
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, is4xxErrorHandler())
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, is5xxErrorHandler())
                .build();
    }

    @Bean
    @Qualifier("alpacaTradingRestClient")
    RestClient alpacaTradingRestClient(AlpacaProperties properties) {
        return RestClient.builder()
                .requestFactory(new ReactorClientHttpRequestFactory(httpClient()))
                .baseUrl(properties.tradingUrl())
                .defaultHeader("APCA-API-KEY-ID", properties.key())
                .defaultHeader("APCA-API-SECRET-KEY", properties.secret())
                .requestInterceptor(new LoggingInterceptor())
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, is4xxErrorHandler())
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, is5xxErrorHandler())
                .build();
    }

    private HttpClient httpClient() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .responseTimeout(Duration.ofSeconds(5));
    }

    private RestClient.ResponseSpec.ErrorHandler is4xxErrorHandler() {
        return (request, response) -> {
            String body = readBody(response);
            log.error("Alpaca API client error (4xx): URI: {}, status={}, body={}",
                    request.getURI(), response.getStatusCode(), body);
            throw new AlpacaApiException(
                    HttpStatus.resolve(response.getStatusCode().value()),
                    "Alpaca API client error: " + body);
        };
    }

    private RestClient.ResponseSpec.ErrorHandler is5xxErrorHandler() {
        return (request, response) -> {
            String body = readBody(response);
            log.error("Alpaca API server error (5xx): URI: {}, status={}, body={}",
                    request.getURI(), response.getStatusCode(), body);
            throw new AlpacaApiException(
                    HttpStatus.resolve(response.getStatusCode().value()),
                    "Alpaca API server error: " + body);
        };
    }

    private String readBody(ClientHttpResponse response) {
        try {
            return StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("Could not read error response body", e);
            return null;
        }
    }
}
