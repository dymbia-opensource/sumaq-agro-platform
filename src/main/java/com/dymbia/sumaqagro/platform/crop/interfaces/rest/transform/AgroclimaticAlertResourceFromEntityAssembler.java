package com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.AgroclimaticAlert;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.AgroclimaticAlertResource;

/**
 * Converts an agroclimatic alert aggregate into an API response resource.
 */
public class AgroclimaticAlertResourceFromEntityAssembler {

    public static AgroclimaticAlertResource toResourceFromEntity(AgroclimaticAlert alert) {
        return new AgroclimaticAlertResource(
                alert.getId(),
                alert.getTitle(),
                alert.getDescription(),
                alert.getSeverity(),
                alert.getRegion(),
                alert.getIssuedAt(),
                alert.getSource()
        );
    }
}
