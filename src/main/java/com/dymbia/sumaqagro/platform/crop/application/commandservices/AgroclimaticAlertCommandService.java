package com.dymbia.sumaqagro.platform.crop.application.commandservices;

import com.dymbia.sumaqagro.platform.crop.domain.model.commands.IssueAgroclimaticAlertCommand;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;

/**
 * Application service contract for issuing agroclimatic alerts.
 */
public interface AgroclimaticAlertCommandService {

    /**
     * Issues a new agroclimatic alert.
     *
     * @param command alert details including region, source, and timestamp
     * @return issued alert identifier or an application error
     */
    Result<Long, ApplicationError> handle(IssueAgroclimaticAlertCommand command);
}
