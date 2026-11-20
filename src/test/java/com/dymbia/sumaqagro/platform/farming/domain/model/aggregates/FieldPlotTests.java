package com.dymbia.sumaqagro.platform.farming.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RegisterFieldPlotCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.events.PlotBoundaryDelineatedEvent;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.GeoCoordinate;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.PlotBoundary;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.PlotStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FieldPlotTests {

    private static FieldPlot registeredPlot() {
        return new FieldPlot(new RegisterFieldPlotCommand(1L, "Parcela Apurímac", "Apurímac", BigDecimal.ONE));
    }

    // A square of about 0.002 degrees per side near Apurímac: roughly 4.8 hectares
    private static PlotBoundary squareBoundary(double side) {
        double lat = -13.6339;
        double lon = -72.8814;
        return new PlotBoundary(List.of(
                new GeoCoordinate(lat, lon),
                new GeoCoordinate(lat, lon + side),
                new GeoCoordinate(lat + side, lon + side),
                new GeoCoordinate(lat + side, lon)));
    }

    @Test
    void newPlotWaitsForItsPolygon() {
        var plot = registeredPlot();

        assertEquals(PlotStatus.WITHOUT_POLYGON, plot.getStatus());
    }

    @Test
    void delineatingTheBoundaryCalculatesTheAreaAndRaisesTheEvent() {
        var plot = registeredPlot();

        plot.delineateBoundary(squareBoundary(0.002));

        assertTrue(plot.getAreaHectares().compareTo(BigDecimal.ONE) > 0);
        assertInstanceOf(PlotBoundaryDelineatedEvent.class, plot.domainEvents().iterator().next());
    }

    @Test
    void boundaryUnderOneHectareIsRejectedWithItsMessageKey() {
        var plot = registeredPlot();

        var exception = assertThrows(IllegalArgumentException.class,
                () -> plot.delineateBoundary(squareBoundary(0.0001)));

        assertEquals("field.plot.error.boundary.areaOutOfRange", exception.getMessage());
    }

    @Test
    void polygonCannotBeLinkedBeforeTheBoundaryIsDelineated() {
        var plot = registeredPlot();

        var exception = assertThrows(IllegalStateException.class, () -> plot.linkPolygon("polygon-1"));

        assertEquals("field.plot.error.boundary.required", exception.getMessage());
    }

    @Test
    void linkingThePolygonActivatesTheMonitoring() {
        var plot = registeredPlot();
        plot.delineateBoundary(squareBoundary(0.002));

        plot.linkPolygon("polygon-1");

        assertEquals(PlotStatus.ACTIVE_MONITORING, plot.getStatus());
        assertEquals("polygon-1", plot.getAgroMonitoringPolygonId());
    }
}
