package com.dymbia.sumaqagro.platform.farming.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.List;

public record FieldPlotResource(
        Long id,
        Long ownerUserId,
        String name,
        String region,
        String status,
        BigDecimal areaHectares,
        String agroMonitoringPolygonId,
        List<GeoCoordinateResource> boundary) {
}
