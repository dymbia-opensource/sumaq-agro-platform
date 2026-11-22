package com.dymbia.sumaqagro.platform.farming.infrastructure.agromonitoring.rest;

import com.dymbia.sumaqagro.platform.farming.application.internal.outboundservices.monitoring.PolygonMonitoringService;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.PlotBoundary;
import com.dymbia.sumaqagro.platform.farming.infrastructure.agromonitoring.rest.resources.AgroMonitoringPolygonRequest;
import com.dymbia.sumaqagro.platform.farming.infrastructure.agromonitoring.rest.resources.AgroMonitoringPolygonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Optional;

/**
 * Implementation of the polygon monitoring service with the AgroMonitoring Polygon API.
 *
 * @since 1.0
 */
@Service
public class AgroMonitoringPolygonService implements PolygonMonitoringService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgroMonitoringPolygonService.class);
    private static final String API_KEY_PARAMETER = "appid";
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(15);

    private final RestClient restClient;
    private final String apiKey;

    public AgroMonitoringPolygonService(@Value("${agromonitoring.base-url}") String baseUrl, @Value("${agromonitoring"
            + ".api-key}") String apiKey) {
        // The polygon is registered inside the PUT request of the plot: without timeouts a slow provider
        // would leave that request hanging. Same limits as the satellite imagery client of Crop.
        var httpClient =
                HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT).followRedirects(HttpClient.Redirect.NEVER).build();
        var requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(READ_TIMEOUT);
        this.restClient = RestClient.builder().baseUrl(baseUrl).requestFactory(requestFactory).build();
        this.apiKey = apiKey;
    }

    @Override
    public Optional<String> registerPolygon(String plotName, PlotBoundary boundary) {
        try {
            var response =
                    restClient.post().uri(uri -> uri.path("/polygons").queryParam(API_KEY_PARAMETER, apiKey).build()).contentType(MediaType.APPLICATION_JSON).body(AgroMonitoringPolygonRequest.fromBoundary(plotName, boundary)).retrieve().body(AgroMonitoringPolygonResponse.class);
            return Optional.ofNullable(response).map(AgroMonitoringPolygonResponse::id);
        } catch (RestClientResponseException exception) {
            LOGGER.warn("AgroMonitoring rejected the polygon of plot '{}': {} {}", plotName,
                    exception.getStatusCode(), exception.getResponseBodyAsString());
        } catch (RestClientException exception) {
            LOGGER.warn("AgroMonitoring could not be reached: {}", exception.getClass().getSimpleName());
        }
        return Optional.empty();
    }

    @Override
    public void removePolygon(String polygonId) {
        try {
            restClient.delete().uri(uri -> uri.path("/polygons/{polygonId}").queryParam(API_KEY_PARAMETER, apiKey).build(polygonId)).retrieve().toBodilessEntity();
        } catch (RestClientResponseException exception) {
            LOGGER.warn("AgroMonitoring did not remove polygon {}: {}", polygonId, exception.getStatusCode());
        } catch (RestClientException exception) {
            LOGGER.warn("AgroMonitoring could not be reached: {}", exception.getClass().getSimpleName());
        }
    }
}
