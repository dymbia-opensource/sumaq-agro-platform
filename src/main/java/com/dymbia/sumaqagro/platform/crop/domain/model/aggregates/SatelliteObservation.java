package com.dymbia.sumaqagro.platform.crop.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.crop.domain.model.commands.RecordSatelliteObservationCommand;
import com.dymbia.sumaqagro.platform.crop.domain.model.events.SatelliteObservationRecordedEvent;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.NdviIndex;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.NdwiIndex;
import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.Percentage;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate root representing a satellite measurement snapshot for a plot.
 * Persistence mapping belongs to the infrastructure layer.
 */
@Getter
public class SatelliteObservation extends AbstractDomainAggregateRoot<SatelliteObservation> {

    private final Long id;
    private final Long plotId;
    private final Instant capturedAt;
    private final String source;
    private final NdviIndex ndviMean;
    private final NdwiIndex ndwiMean;
    private final Percentage cloudCoveragePercent;
    private final Double surfaceTempKelvin;

    /**
     * Initializes validated data without registering events.
     * A new observation has no persistence identifier yet.
     */
    private SatelliteObservation(Long id, RecordSatelliteObservationCommand command) {
        Objects.requireNonNull(command, "Command is required");

        this.id = id;
        this.plotId = command.plotId();
        this.capturedAt = command.capturedAt();
        this.source = command.source();
        this.ndviMean = command.ndviMean();
        this.ndwiMean = command.ndwiMean();
        this.cloudCoveragePercent = command.cloudCoveragePercent();
        this.surfaceTempKelvin = command.surfaceTempKelvin();
    }

    /**
     * Creates a new observation and registers its measurement event.
     * The repository adapter will publish the event after saving.
     */
    public SatelliteObservation(RecordSatelliteObservationCommand command) {
        this(null, command);

        registerDomainEvent(new SatelliteObservationRecordedEvent(
                this,
                this.plotId,
                this.capturedAt,
                this.source,
                this.ndviMean,
                this.ndwiMean,
                this.cloudCoveragePercent,
                this.surfaceTempKelvin
        ));
    }

    /**
     * Restores an existing observation without producing another recording event.
     */
    public static SatelliteObservation reconstitute(
            Long id,
            Long plotId,
            Instant capturedAt,
            String source,
            NdviIndex ndviMean,
            NdwiIndex ndwiMean,
            Percentage cloudCoveragePercent,
            Double surfaceTempKelvin
    ) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Satellite observation ID must be greater than zero");
        }

        var command = new RecordSatelliteObservationCommand(
                plotId,
                capturedAt,
                source,
                ndviMean,
                ndwiMean,
                cloudCoveragePercent,
                surfaceTempKelvin
        );

        return new SatelliteObservation(id, command);
    }
}
