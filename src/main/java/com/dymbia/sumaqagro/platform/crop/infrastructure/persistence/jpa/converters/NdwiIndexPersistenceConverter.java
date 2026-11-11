package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.converters;

import com.dymbia.sumaqagro.platform.crop.domain.model.valueobjects.NdwiIndex;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts NDWI value objects to database values and vice versa.
 */

@Converter
public class NdwiIndexPersistenceConverter implements AttributeConverter<NdwiIndex, Double> {

    @Override
    public Double convertToDatabaseColumn(NdwiIndex attribute) {
        return attribute == null ? null : attribute.value();
    }

    @Override
    public NdwiIndex convertToEntityAttribute(Double dbData) {
        return dbData == null ? null : new NdwiIndex(dbData);
    }

}
