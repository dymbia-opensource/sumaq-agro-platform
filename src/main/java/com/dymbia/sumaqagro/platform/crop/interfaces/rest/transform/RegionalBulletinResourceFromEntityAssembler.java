package com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.RegionalBulletin;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.RegionalBulletinResource;

/**
 * Converts a regional bulletin aggregate into an API response.
 */
public class RegionalBulletinResourceFromEntityAssembler {

    /**
     * Converts a {@link RegionalBulletin} to a {@link RegionalBulletinResource}.
     *
     * @param bulletin The {@link RegionalBulletin} to convert.
     * @return The {@link RegionalBulletinResource} that results from the conversion.
     */
    public static RegionalBulletinResource toResourceFromEntity(RegionalBulletin bulletin) {
        return new RegionalBulletinResource(
                bulletin.getId(),
                bulletin.getAgronomistId(),
                bulletin.getRegion(),
                bulletin.getTitle(),
                bulletin.getContent(),
                bulletin.getPublishedAt()
        );
    }
}
