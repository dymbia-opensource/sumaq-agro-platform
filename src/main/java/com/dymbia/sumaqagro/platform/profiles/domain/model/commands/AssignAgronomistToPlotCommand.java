package com.dymbia.sumaqagro.platform.profiles.domain.model.commands;

/**
 * Command to assign an agronomist to a plot of a cooperative.
 *
 * @param cooperativeId    identity of the cooperative
 * @param agronomistUserId identity of the agronomist (user from IAM)
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