package com.dymbia.sumaqagro.platform.crop.infrastructure.agromonitoring.rest;

import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.monitoring.WeatherForecasting;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of {@link WeatherForecasting} with the AgroMonitoring Weather API
 * ({@code GET /weather/forecast?polyid=...}).
 * <p>
 * AgroMonitoring answers 5 days of forecast in 3-hour steps, with the temperature in Kelvin.
 * </p>
 *
 * @since 1.1
 */
@Component
public class AgroMonitoringWeatherForecast implements WeatherForecasting {

    private static final double KELVIN_OFFSET = 273.15;

    private final String apiKey;
    private final RestClient client;

    public AgroMonitoringWeatherForecast(@Value("${agromonitoring.base-url}") String baseUrl,
                                         @Value("${agromonitoring.api-key}") String apiKey) {
        this.apiKey = apiKey;
        var httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
        var requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(15));
        this.client = RestClient.builder().baseUrl(baseUrl).requestFactory(requestFactory).build();
    }

    @Override
    public List<ForecastPoint> findForecast(String polygonId) {
        try {
            var items = client.get()
                    .uri(uri -> uri.path("/weather/forecast")
                            .queryParam("polyid", polygonId)
                            .queryParam("appid", apiKey)
                            .build())
                    .retrieve()
                    .body(Item[].class);
            if (items == null) {
                throw new Unavailable();
            }
            return Arrays.stream(items)
                    .filter(Objects::nonNull)
                    .filter(Item::isComplete)
                    .map(Item::toForecastPoint)
                    .sorted(Comparator.comparing(ForecastPoint::at))
                    .toList();
        } catch (RestClientException | IllegalArgumentException exception) {
            // RestClient errors can contain the request URL with the API key, so they are not logged
            throw new Unavailable();
        }
    }

    private record Item(Long dt, Main main, Rain rain) {
        boolean isComplete() {
            return dt != null && main != null && main.temp() != null;
        }

        ForecastPoint toForecastPoint() {
            var rainMillimeters = rain == null || rain.threeHours() == null ? 0.0 : rain.threeHours();
            return new ForecastPoint(Instant.ofEpochSecond(dt), main.temp() - KELVIN_OFFSET, rainMillimeters);
        }
    }

    private record Main(Double temp) {
    }

    private record Rain(@JsonProperty("3h") Double threeHours) {
    }
}
