package com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.converters;

import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.SeedVariety;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Stores a {@link SeedVariety} as a plain string column
 *
 * @since 1.0
 */
@Converter
public class SeedVarietyAttributeConverter implements AttributeConverter<SeedVariety, String> {

    @Override
    public String convertToDatabaseColumn(SeedVariety attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public SeedVariety convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new SeedVariety(dbData);
    }

}
