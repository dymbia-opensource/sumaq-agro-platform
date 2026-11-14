package com.dymbia.sumaqagro.platform.crop.application.commandservices;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.RegionalBulletin;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.PublishRegionalBulletinCommand;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;

/**
 * Application service contract for publishing regional bulletins.
 */
public interface RegionalBulletinCommandService {

    /**
     * Handles the publishing of a new regional bulletin.
     *
     * @param command command containing regional bulletin data
     * @return created regional bulletin or an application error
     * @see PublishRegionalBulletinCommand
     */
    Result<RegionalBulletin, ApplicationError> handle(PublishRegionalBulletinCommand command);
}
