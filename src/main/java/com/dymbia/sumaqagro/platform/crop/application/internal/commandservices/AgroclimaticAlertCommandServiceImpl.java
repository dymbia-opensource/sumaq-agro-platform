package com.dymbia.sumaqagro.platform.crop.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.crop.application.commandservices.AgroclimaticAlertCommandService;
import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.AgroclimaticAlert;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.IssueAgroclimaticAlertCommand;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.AgroclimaticAlertRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Issues an agroclimatic alert and returns its persisted identifier.
 */
@Service
public class AgroclimaticAlertCommandServiceImpl implements AgroclimaticAlertCommandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgroclimaticAlertCommandServiceImpl.class);

    private final AgroclimaticAlertRepository alertRepository;

    public AgroclimaticAlertCommandServiceImpl(AgroclimaticAlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Override
    public Result<Long, ApplicationError> handle(IssueAgroclimaticAlertCommand command) {
        try {
            var alert = new AgroclimaticAlert(command);
            var savedAlert = alertRepository.save(alert);

            return Result.success(savedAlert.getId());
        } catch (RuntimeException e) {
            LOGGER.error("Failed to issue agroclimatic alert", e);
            return Result.failure(ApplicationError.unexpected(
                    "issue-agroclimatic-alert",
                    "Could not issue the agroclimatic alert"
            ));
        }
    }
}
