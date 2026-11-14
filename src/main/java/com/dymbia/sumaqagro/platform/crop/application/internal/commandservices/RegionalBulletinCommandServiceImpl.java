package com.dymbia.sumaqagro.platform.crop.application.internal.commandservices;

import com.dymbia.sumaqagro.platform.crop.application.commandservices.RegionalBulletinCommandService;
import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.RegionalBulletin;
import com.dymbia.sumaqagro.platform.crop.domain.model.commands.PublishRegionalBulletinCommand;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.RegionalBulletinRepository;
import com.dymbia.sumaqagro.platform.shared.application.result.ApplicationError;
import com.dymbia.sumaqagro.platform.shared.application.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Publishes regional bulletins and returns the saved bulletin.
 */
@Service
public class RegionalBulletinCommandServiceImpl implements RegionalBulletinCommandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegionalBulletinCommandServiceImpl.class);

    private final RegionalBulletinRepository bulletinRepository;

    public RegionalBulletinCommandServiceImpl(RegionalBulletinRepository bulletinRepository) {
        this.bulletinRepository = bulletinRepository;
    }

    @Override
    public Result<RegionalBulletin, ApplicationError> handle(PublishRegionalBulletinCommand command) {
        try {
            var bulletin = new RegionalBulletin(command, Instant.now());
            return Result.success(bulletinRepository.save(bulletin));
        } catch (RuntimeException exception) {
            LOGGER.error("Failed to publish regional bulletin", exception);
            return Result.failure(ApplicationError.unexpected(
                    "publish-regional-bulletin",
                    "Could not publish the regional bulletin"
            ));
        }
    }
}
