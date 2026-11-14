package com.dymbia.sumaqagro.platform.crop.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.crop.domain.model.commands.PublishRegionalBulletinCommand;
import com.dymbia.sumaqagro.platform.crop.interfaces.rest.resources.PublishRegionalBulletinResource;

/**
 * Converts an API request into a regional bulletin publication command.
 */
public class PublishRegionalBulletinCommandFromResourceAssembler {

    /**
     * Converts a {@link PublishRegionalBulletinResource} to a {@link PublishRegionalBulletinCommand}.
     *
     * @param resource The {@link PublishRegionalBulletinResource} to convert.
     * @return The {@link PublishRegionalBulletinCommand} that results from the conversion.
     */
    public static PublishRegionalBulletinCommand toCommandFromResource(PublishRegionalBulletinResource resource) {
        return new PublishRegionalBulletinCommand(
                resource.agronomistId(),
                resource.region(),
                resource.title(),
                resource.content()
        );
    }
}
