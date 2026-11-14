package com.dymbia.sumaqagro.platform.crop.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.crop.domain.model.commands.PublishRegionalBulletinCommand;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Aggregate root representing a bulletin published for a region.
 */
@Getter
public class RegionalBulletin extends AbstractDomainAggregateRoot<RegionalBulletin> {

    private final Long id;
    private final Long agronomistId;
    private final String region;
    private final String title;
    private final String content;
    private final Instant publishedAt;

    private RegionalBulletin(Long id, PublishRegionalBulletinCommand command, Instant publishedAt) {
        Objects.requireNonNull(command, "Command is required");

        this.id = id;
        this.agronomistId = command.agronomistId();
        this.region = command.region();
        this.title = command.title();
        this.content = command.content();
        this.publishedAt = Objects.requireNonNull(
                publishedAt, "Publication timestamp is required"
        );
    }

    /**
     * Creates a new regional bulletin.
     */
    public RegionalBulletin(PublishRegionalBulletinCommand command, Instant publishedAt) {
        this(null, command, publishedAt);
    }

    /**
     * Restores a bulletin from its persisted state.
     */
    public static RegionalBulletin reconstitute(
            Long id,
            Long agronomistId,
            String region,
            String title,
            String content,
            Instant publishedAt
    ) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Regional bulletin ID must be greater than zero"
            );
        }

        var command = new PublishRegionalBulletinCommand(
                agronomistId, region, title, content
        );

        return new RegionalBulletin(id, command, publishedAt);
    }
}