package com.dymbia.sumaqagro.platform.farming.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RegisterFieldPlotCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.events.PlotBoundaryDelineatedEvent;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.PlotBoundary;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.PlotStatus;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.SoilBaseline;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Field plot aggregate root
 * <p>
 * A piece of land that belongs to a producer. It starts without a polygon and changes its status.
 * </p>
 * <p>
 * It is a plain domain class: the JPA mapping lives in {@code FieldPlotPersistenceEntity}.
 * </p>
 *
 */
@Getter
public class FieldPlot extends AbstractDomainAggregateRoot<FieldPlot> {

    // Area limits of the monitoring provider
    private static final BigDecimal MIN_POLYGON_HECTARES = BigDecimal.ONE;
    private static final BigDecimal MAX_POLYGON_HECTARES = BigDecimal.valueOf(3000);

    private Long id;
    private Long ownerUserId;
    private String name;
    private String region;
    private PlotStatus status;
    private BigDecimal areaHectares;
    private String agroMonitoringPolygonId;

    private PlotBoundary boundary;

    private SoilBaseline soilBaseline;

    private FieldPlot(Long id, Long ownerUserId, String name, String region, PlotStatus status,
                      BigDecimal areaHectares, String agroMonitoringPolygonId) {
        this.id = id;
        this.ownerUserId = ownerUserId;
        this.name = name;
        this.region = region;
        this.status = status;
        this.areaHectares = areaHectares;
        this.agroMonitoringPolygonId = agroMonitoringPolygonId;
    }

    /**
     * Creates a new field plot from the register command
     *
     * @param command the register field plot command
     */
    public FieldPlot(RegisterFieldPlotCommand command) {
        this(null, command.ownerUserId(), command.name(), command.region(), PlotStatus.WITHOUT_POLYGON,
                command.declaredAreaHectares(), null);

    }

    /**
     * Rebuilds a field plot that already exists in the database
     *
     * @param id                      the store id
     * @param ownerUserId             the producer that owns the plot
     * @param name                    the plot name
     * @param region                  the plot region
     * @param status                  the monitoring status
     * @param areaHectares            the area in hectares
     * @param agroMonitoringPolygonId the agromonitoring polygon id
     * @param boundary                the polygon of the plot, may be null
     * @param soilBaseline            the soil analysis, may be null
     * @return the field plot with the store state
     */
    public static FieldPlot reconstitute(Long id, Long ownerUserId, String name, String region, PlotStatus status,
                                         BigDecimal areaHectares, String agroMonitoringPolygonId,
                                         PlotBoundary boundary, SoilBaseline soilBaseline) {
        var fieldPlot = new FieldPlot(id, ownerUserId, name, region, status, areaHectares, agroMonitoringPolygonId);
        fieldPlot.boundary = boundary;
        fieldPlot.soilBaseline = soilBaseline;
        return fieldPlot;
    }

    /**
     * Delineates the boundary of the field plot
     *
     * @param boundary the plot boundary
     * @throws IllegalArgumentException if the boundary is null or its area is out of range
     */
    public void delineateBoundary(PlotBoundary boundary) {
        if (boundary == null) {
            throw new IllegalArgumentException("field.plot.error.boundary.required");
        }
        var area = boundary.calculateArea();
        if (area.compareTo(MIN_POLYGON_HECTARES) < 0 || area.compareTo(MAX_POLYGON_HECTARES) > 0) {
            throw new IllegalArgumentException("field.plot.error.boundary.areaOutOfRange");
        }
        var previousPolygonId = this.agroMonitoringPolygonId;
        this.boundary = boundary;
        this.areaHectares = area;
        this.agroMonitoringPolygonId = null;
        this.status = PlotStatus.WITHOUT_POLYGON;
        registerDomainEvent(new PlotBoundaryDelineatedEvent(id, name, boundary, previousPolygonId));
    }

    /**
     * Links a polygon to the field plot
     *
     * @param polygonId the polygon id
     * @throws IllegalArgumentException if the polygon id is null or blank
     * @throws IllegalStateException    if the field plot does not have a boundary
     */
    public void linkPolygon(String polygonId) {
        if (polygonId == null || polygonId.isBlank()) {
            throw new IllegalArgumentException("field.plot.error.polygonId.notBlank");
        }
        if (!hasPolygon()) {
            throw new IllegalStateException("field.plot.error.boundary.required");
        }
        this.agroMonitoringPolygonId = polygonId;
        this.status = PlotStatus.ACTIVE_MONITORING;
    }

    /**
     * Checks if the polygon of the field plot was already drawn
     *
     * @return {@code true} if the field plot has a boundary, {@code false} otherwise
     */
    public boolean hasPolygon() {
        return boundary != null;
    }

    /**
     * Records the initial soil analysis of the plot. A new analysis replaces the previous one.
     *
     * @param soilBaseline the soil analysis
     * @throws IllegalArgumentException if the soil analysis is null
     */
    public void recordSoilBaseline(SoilBaseline soilBaseline) {
        if (soilBaseline == null) {
            throw new IllegalArgumentException("field.plot.error.soil.required");
        }
        this.soilBaseline = soilBaseline;
    }

    /**
     * Checks if the soil analysis of the field plot was already recorded
     *
     * @return {@code true} if the field plot has a soil analysis, {@code false} otherwise
     */
    public boolean hasSoilBaseline() {
        return soilBaseline != null;
    }
}