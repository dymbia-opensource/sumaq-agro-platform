package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.converters;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.Percentage;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts Percentage value objects to database values and vice versa.
 */

@Converter
public class PercentagePersistenceConverter implements AttributeConverter<Percentage, Double> {

    @Override
    public Double convertToDatabaseColumn (Percentage attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public Percentage convertToEntityAttribute (Double dbData) {
        return dbData == null ? null : new Percentage(dbData);
    }

}
