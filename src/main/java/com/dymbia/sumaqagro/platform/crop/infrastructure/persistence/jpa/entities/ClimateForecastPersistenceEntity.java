package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.entities;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.Percentage;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.converters.PercentagePersistenceConverter;
import com.dymbia.sumaqagro.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * JPA persistence entity for climate forecasts.
 */
@Entity
@Table(name = "climate_forecasts")
@Getter
@Setter
@NoArgsConstructor
public class ClimateForecastPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String region;

    @Column(nullable = false)
    private double temperatureCelsius;

    @Convert(converter = PercentagePersistenceConverter.class)
    @Column(nullable = false)
    private Percentage rainChance;

    @Column(nullable = false)
    private Instant forecastDate;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String source;
}
