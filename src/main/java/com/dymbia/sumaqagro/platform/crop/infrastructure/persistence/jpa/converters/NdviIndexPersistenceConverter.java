package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.converters;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.NdviIndex;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts NDVI value objects to database values and vice versa.
 */
@Converter
public class NdviIndexPersistenceConverter implements AttributeConverter<NdviIndex, Double> {

    @Override
    public Double convertToDatabaseColumn(NdviIndex attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public NdviIndex convertToEntityAttribute(Double dbData) {
        return dbData == null ? null : new NdviIndex(dbData);
    }

}