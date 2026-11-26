package com.dymbia.sumaqagro.platform.crop.infrastructure.agromonitoring.rest;

import com.dymbia.sumaqagro.platform.crop.application.internal.outboundservices.monitoring.SatelliteRasterImagery;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.SatelliteIndex;
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
import java.util.*;
import java.util.regex.Pattern;

/** Proxies the provider's original PNG tiles. Remote links and keys stay inside this adapter. */
@Component
public class AgroMonitoringSatelliteRasterImagery implements SatelliteRasterImagery {
    private final URI origin;
    private final String apiKey;
    private final RestClient client;
    private final Map<CaptureKey, CachedScene> scenes = new LinkedHashMap<>();
    private record CaptureKey(String polygonId, Instant capturedAt, String source) { }
    private record CachedScene(Instant expiresAt, Map<SatelliteIndex, String> paths) { }
    private record Scene(Long dt, String type, Links tile) { }
    private record Links(String ndvi, String ndwi) { }

    public AgroMonitoringSatelliteRasterImagery(@Value("${agromonitoring.base-url}") String baseUrl,
                                                @Value("${agromonitoring.api-key}") String apiKey) {
        this.origin = URI.create(baseUrl); this.apiKey = apiKey;
        var http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NEVER).build();
        var factory = new JdkClientHttpRequestFactory(http);
        factory.setReadTimeout(Duration.ofSeconds(15));
        this.client = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }
    @Override
    public Set<SatelliteIndex> findAvailableIndices(String polygonId, Instant capturedAt, String source) {
        return scenePaths(new CaptureKey(polygonId, capturedAt, source)).keySet();
    }
    @Override
    public Optional<byte[]> readTile(String polygonId, Instant capturedAt, String source,
                                     SatelliteIndex index, int zoom, int x, int y) {
        if (zoom < 0 || zoom > 22 || x < 0 || y < 0 || x >= (1 << zoom) || y >= (1 << zoom)) throw new Unavailable();
        var path = scenePaths(new CaptureKey(polygonId, capturedAt, source)).get(index);
        if (path == null) return Optional.empty();
        path = path.replace("{z}", Integer.toString(zoom)).replace("{x}", Integer.toString(x)).replace("{y}", Integer.toString(y));
        try {
            return client.get().uri(providerUri(path, index == SatelliteIndex.NDVI)).exchange((request, response) -> {
                if (response.getStatusCode().value() == 404) return Optional.empty();
                if (!response.getStatusCode().is2xxSuccessful()) throw new Unavailable();
                var type = response.getHeaders().getContentType();
                if (type == null || !"image/png".equalsIgnoreCase(type.toString().split(";")[0])) throw new Unavailable();
                var bytes = response.getBody().readNBytes(1024 * 1024 + 1);
                byte[] png = {(byte)137,80,78,71,13,10,26,10};
                if (bytes.length > 1024 * 1024 || bytes.length < 8 || !Arrays.equals(png, Arrays.copyOf(bytes, 8))) throw new Unavailable();
                return Optional.of(bytes);
            });
        } catch (RestClientException | IllegalArgumentException exception) { throw new Unavailable(); }
    }
    private URI providerUri(String path, boolean greenPalette) {
        var builder = UriComponentsBuilder.fromUri(origin).replacePath(path).replaceQuery(null).queryParam("appid", apiKey);
        if (greenPalette) builder.queryParam("paletteid", 1);
        return builder.build().encode().toUri();
    }
    private synchronized Map<SatelliteIndex, String> scenePaths(CaptureKey key) {
        var cached = scenes.get(key);
        if (cached != null && cached.expiresAt().isAfter(Instant.now())) return cached.paths();
        try {
            var result = client.get().uri(uri -> uri.path("/image/search")
                    .queryParam("polyid", key.polygonId()).queryParam("start", key.capturedAt().getEpochSecond() - 1)
                    .queryParam("end", key.capturedAt().getEpochSecond() + 1).queryParam("appid", apiKey).build())
                    .retrieve().body(Scene[].class);
            if (result == null) throw new Unavailable();
            var scene = Arrays.stream(result).filter(Objects::nonNull)
                    .filter(item -> item.dt() != null && item.dt() == key.capturedAt().getEpochSecond() && key.source().equals(item.type()))
                    .findFirst();
            var paths = new EnumMap<SatelliteIndex, String>(SatelliteIndex.class);
            if (scene.isPresent() && scene.get().tile() != null) {
                addPath(paths, SatelliteIndex.NDVI, scene.get().tile().ndvi(), key.polygonId());
                addPath(paths, SatelliteIndex.NDWI, scene.get().tile().ndwi(), key.polygonId());
            }
            var immutable = Map.copyOf(paths);
            scenes.entrySet().removeIf(entry -> entry.getValue().expiresAt().isBefore(Instant.now()));
            if (scenes.size() >= 128) scenes.remove(scenes.keySet().iterator().next());
            scenes.put(key, new CachedScene(Instant.now().plusSeconds(300), immutable));
            return immutable;
        } catch (RestClientException | IllegalArgumentException exception) { throw new Unavailable(); }
    }
    private void addPath(Map<SatelliteIndex, String> paths, SatelliteIndex index, String link, String polygonId) {
        if (link == null || link.isBlank()) return;
        // URI templates contain braces: encode them only for parsing and validate the decoded path.
        var uri = URI.create(link.replace("{", "%7B").replace("}", "%7D"));
        var expected = "/tile/1\\.0/\\{z}/\\{x}/\\{y}/[a-zA-Z0-9_-]+/" + Pattern.quote(polygonId);
        if (!origin.getHost().equalsIgnoreCase(uri.getHost()) || uri.getUserInfo() != null
                || !("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                || !uri.getPath().matches(expected)) throw new Unavailable();
        paths.put(index, uri.getPath());
    }
}
