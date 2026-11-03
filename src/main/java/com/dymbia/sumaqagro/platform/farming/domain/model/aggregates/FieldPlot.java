package com.dymbia.sumaqagro.platform.farming.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.RegisterFieldPlotCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.PlotStatus;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
@Getter
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"owner_user_id", "name"},name=FieldPlot.OWNER_USER_ID_NAME_UNIQUE_CONSTRAINT)
})
public class FieldPlot extends AuditableAbstractAggregateRoot <FieldPlot> {
    public static final String OWNER_USER_ID_NAME_UNIQUE_CONSTRAINT = "uk_owner_user_id_name";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ownerUserId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 150)
    private String region;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PlotStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal areaHectares;

    // Set when the polygon is created in AgroMonitoring
    private String agroMonitoringPolygonId;

    // Required by JPA
    protected FieldPlot(){}

    public FieldPlot(RegisterFieldPlotCommand command) {
        this.ownerUserId = command.ownerUserId();
        this.name = command.name();
        this.region = command.region();
        this.areaHectares = command.declaredAreaHectares();
        this.status = PlotStatus.WITHOUT_POLYGON;
    }
}
