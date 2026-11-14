package com.dymbia.sumaqagro.platform.profiles.domain.model.commands;

/**
 * Command used by the cooperative director to assign an agronomist to a plot (US-20).
 *
 * @param cooperativeId    cooperative that makes the assignment (required)
 * @param agronomistUserId IAM user id of the agronomist
 * @param plotId           identity of the plot (from Field Management)
 * @since 1.0
 */
public record AssignAgronomistToPlotCommand(
        Long cooperativeId,
        Long agronomistUserId,
        Long plotId
) {
    public AssignAgronomistToPlotCommand {
        if (cooperativeId == null || cooperativeId <= 0) {
            throw new IllegalArgumentException("Cooperative id must be a positive number");
        }
        if (agronomistUserId == null || agronomistUserId <= 0) {
            throw new IllegalArgumentException("Agronomist user id must be a positive number");
        }
        if (plotId == null || plotId <= 0) {
            throw new IllegalArgumentException("Plot id must be a positive number");
        }
    }
}
