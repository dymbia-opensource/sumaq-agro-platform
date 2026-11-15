package com.dymbia.sumaqagro.platform.crop.infrastructure.agromonitoring.rest;

import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.monitoring.SatelliteImagery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

@Component
public class AgroMonitoringSatelliteImagery implements SatelliteImagery {

    private final URI baseUri;
    private final String apiKey;
    private final RestClient client;

    public AgroMonitoringSatelliteImagery(
            @Value("${agromonitoring.base-url}") String baseUrl,
            @Value("${agromonitoring.api-key}") String apiKey
    ) {
        this.baseUri = URI.create(baseUrl);
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
    public Optional<Measurement> findLatest(String polygonId, Instant start, Instant end) {
        try {
            var scenes = client.get()
                    .uri(uri -> uri.path("/image/search")
                            .queryParam("polyid", polygonId)
                            .queryParam("start", start.getEpochSecond())
                            .queryParam("end", end.getEpochSecond())
                            .queryParam("appid", apiKey)
                            .build())
                    .retrieve()
                    .body(Scene[].class);
            if (scenes == null) {
                throw new Unavailable();
            }

            return Arrays.stream(scenes)
                    .filter(scene -> scene != null && scene.hasStatistics())
                    .sorted(Comparator.comparing(Scene::dt).reversed().thenComparing(Scene::type))
                    .map(scene -> readMeasurement(scene, polygonId))
                    .flatMap(Optional::stream)
                    .findFirst();
        } catch (RestClientException | IllegalArgumentException e) {
            // RestClient errors can contain the request URL with the API key.
            throw new Unavailable();
        }
    }

    private Optional<Measurement> readMeasurement(Scene scene, String polygonId) {
        var ndvi = readMean(scene.stats().ndvi(), polygonId);
        var ndwi = readMean(scene.stats().ndwi(), polygonId);
        if (ndvi == null && ndwi == null) {
            return Optional.empty();
        }
        return Optional.of(new Measurement(Instant.ofEpochSecond(scene.dt()), scene.type(),
                ndvi, ndwi, scene.cl()));
    }

    private Double readMean(String link, String polygonId) {
        if (link == null || link.isBlank()) {
            return null;
        }
        var statsUri = URI.create(link);
        var path = statsUri.getPath();
        var expectedPath = "/stats/1\\.0/[a-zA-Z0-9_-]+/" + java.util.regex.Pattern.quote(polygonId);
        if (!baseUri.getHost().equalsIgnoreCase(statsUri.getHost())
                || path == null || !path.matches(expectedPath)) {
            throw new Unavailable();
        }

        // Use the configured origin and key, even when the response link uses HTTP.
        var uri = UriComponentsBuilder.fromUri(baseUri)
                .replacePath(path)
                .replaceQuery(null)
                .queryParam("appid", apiKey)
                .build()
                .encode()
                .toUri();
        var statistics = client.get().uri(uri).retrieve().body(Statistics.class);
        return statistics == null ? null : statistics.mean();
    }

    private record Scene(Long dt, String type, Double cl, Links stats) {
        boolean hasStatistics() {
            return dt != null && type != null && !type.isBlank() && cl != null
                    && stats != null && (stats.ndvi() != null || stats.ndwi() != null);
        }
    }

    private record Links(String ndvi, String ndwi) {
    }

    private record Statistics(Double mean) {
    }
}
