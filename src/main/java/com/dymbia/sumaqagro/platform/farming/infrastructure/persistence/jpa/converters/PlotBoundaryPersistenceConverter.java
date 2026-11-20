package com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.converters;

import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.GeoCoordinate;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.PlotBoundary;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Stores a {@link PlotBoundary} as text, with the format {@code lat,lon;lat,lon;lat,lon}.
 *
 * @since 1.0
 */
@Converter
public class PlotBoundaryAttributeConverter implements AttributeConverter<PlotBoundary, String> {
    @Override
    public String convertToDatabaseColumn(PlotBoundary attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.vertices().stream().map(vertex -> vertex.latitude() + "," + vertex.longitude()).collect(Collectors.joining(";"));
    }

    @Override
    public PlotBoundary convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        var vertices =
                Arrays.stream(dbData.split(";")).map(vertex -> vertex.split(",")).map(parts -> new GeoCoordinate(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]))).toList();
        return new PlotBoundary(vertices);
    }
}
