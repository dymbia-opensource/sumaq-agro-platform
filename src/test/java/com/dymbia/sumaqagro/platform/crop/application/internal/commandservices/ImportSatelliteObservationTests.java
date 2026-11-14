package com.dymbia.sumaqagro.platform.crop.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.SatelliteObservation;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.ImportSatelliteObservationCommand;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.SatelliteObservationRepository;
import com.dymbia.sumaqagro.platform.crop.infrastructure.agromonitoring.rest.AgroMonitoringSatelliteImagery;
import com.dymbia.sumaqagro.platform.farming.application.queryservices.FieldPlotQueryService;
import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.FieldPlot;
import com.dymbia.sumaqagro.platform.farming.domain.model.queries.GetFieldPlotByIdQuery;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ImportSatelliteObservationTests {

    private static final Instant START = Instant.parse("2022-04-05T23:00:00Z");
    private static final Instant END = Instant.parse("2022-04-06T01:00:00Z");
    private static final Long PLOT_ID = 3L;
    private static final Long OWNER_ID = 1L;

    private final ConcurrentLinkedQueue<String> requests = new ConcurrentLinkedQueue<>();
    private final SatelliteObservationRepository observations = mock(SatelliteObservationRepository.class);
    private HttpServer provider;
    private SatelliteObservationCommandServiceImpl service;
    private String scenes = "[]";

    @BeforeEach
    void setUp() throws IOException {
        provider = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        provider.createContext("/", exchange -> {
            try (exchange) {
                var uri = exchange.getRequestURI();
                requests.add(uri.toString());
                var body = switch (uri.getPath()) {
                    case "/agro/1.0/image/search" -> scenes;
                    case "/stats/1.0/ndvi-scene/test-polygon" -> "{\"mean\":0.42}";
                    case "/stats/1.0/ndwi-scene/test-polygon" -> "{\"mean\":0.17}";
                    default -> null;
                };
                if (body == null) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }
                var bytes = body.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
            }
        });
        provider.start();

        var fieldPlots = mock(FieldPlotQueryService.class);
        var plot = mock(FieldPlot.class);
        when(plot.getOwnerUserId()).thenReturn(OWNER_ID);
        when(plot.getAgroMonitoringPolygonId()).thenReturn("test-polygon");
        when(fieldPlots.handle(new GetFieldPlotByIdQuery(PLOT_ID))).thenReturn(Optional.of(plot));

        var baseUrl = "http://127.0.0.1:" + provider.getAddress().getPort() + "/agro/1.0";
        var imagery = new AgroMonitoringSatelliteImagery(baseUrl, "test-key");
        service = new SatelliteObservationCommandServiceImpl(observations, fieldPlots, imagery);
    }

    @AfterEach
    void stopProvider() {
        provider.stop(0);
    }

    @Test
    void importsBothIndicesForTheLinkedPolygon() {
        scenes = """
                [{
                  "dt": 1649203200,
                  "type": "Landsat 8",
                  "cl": 8.7,
                  "dc": 100,
                  "stats": {
                    "ndvi": "http://127.0.0.1/stats/1.0/ndvi-scene/test-polygon?appid=unused",
                    "ndwi": "http://127.0.0.1/stats/1.0/ndwi-scene/test-polygon?appid=unused"
                  }
                }]
                """;
        when(observations.saveImported(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.handle(new ImportSatelliteObservationCommand(PLOT_ID, OWNER_ID, START, END));

        assertTrue(result.isSuccess());
        var saved = ArgumentCaptor.forClass(SatelliteObservation.class);
        verify(observations).saveImported(saved.capture());
        assertEquals(PLOT_ID, saved.getValue().getPlotId());
        assertEquals(Instant.parse("2022-04-06T00:00:00Z"), saved.getValue().getCapturedAt());
        assertEquals("Landsat 8", saved.getValue().getSource());
        assertEquals(0.42, saved.getValue().getNdviMean().value());
        assertEquals(0.17, saved.getValue().getNdwiMean().value());
        assertEquals(8.7, saved.getValue().getCloudCoveragePercent().value());
        assertEquals("/agro/1.0/image/search?polyid=test-polygon&start=1649199600&end=1649206800&appid=test-key",
                requests.poll());
        assertEquals("/stats/1.0/ndvi-scene/test-polygon?appid=test-key", requests.poll());
        assertEquals("/stats/1.0/ndwi-scene/test-polygon?appid=test-key", requests.poll());
        assertTrue(requests.isEmpty());
    }

    @Test
    void emptySearchDoesNotWriteAnObservation() {
        var result = service.handle(new ImportSatelliteObservationCommand(PLOT_ID, OWNER_ID, START, END));

        assertTrue(result.isFailure());
        verifyNoInteractions(observations);
        assertEquals(1, requests.size());
    }

    @Test
    void anotherOwnerCannotRequestImageryForThePlot() {
        var result = service.handle(new ImportSatelliteObservationCommand(PLOT_ID, 2L, START, END));

        assertTrue(result.isFailure());
        verifyNoInteractions(observations);
        assertTrue(requests.isEmpty());
    }
}
