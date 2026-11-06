package com.dymbia.sumaqagro.platform.farming.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RegisterFieldPlotCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.PlotStatus;
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
    private final Long id;
    private final Long ownerUserId;
    private final String name;
    private final String region;
    private final PlotStatus status;
    private final BigDecimal areaHectares;
    private final String agroMonitoringPolygonId;

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
     *
     * @return the field plot with the store state
     */
    public static FieldPlot reconstitute(Long id, Long ownerUserId, String name, String region, PlotStatus status,
                                         BigDecimal areaHectares, String agroMonitoringPolygonId) {
        return new FieldPlot(id, ownerUserId, name, region, status, areaHectares, agroMonitoringPolygonId);
    }
}