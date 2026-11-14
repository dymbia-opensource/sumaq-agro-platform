package com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects;

import java.time.Instant;
import java.util.Objects;

/**
 * One past period in which an agronomist attended a plot.
 *
 * <p>It is a value object: once written it never changes. it is never looked up by id.</p>
 *
 * @param agronomistUserId the agronomist that attended the plot
 * @param assignedAt       when that agronomist was assigned
 * @param endedAt          when that agronomist stopped attending the plot
 * @since 1.0
 */
public record AssignmentHistoryEntry(Long agronomistUserId, Instant assignedAt, Instant endedAt) {

    public AssignmentHistoryEntry {
        if (agronomistUserId == null || agronomistUserId <= 0) {
            throw new IllegalArgumentException("Agronomist user id must be a positive number");
        }
        Objects.requireNonNull(assignedAt, "assignedAt must not be null");
        Objects.requireNonNull(endedAt, "endedAt must not be null");
        if (endedAt.isBefore(assignedAt)) {
            throw new IllegalArgumentException("endedAt must not be before assignedAt");
        }
    }
}
