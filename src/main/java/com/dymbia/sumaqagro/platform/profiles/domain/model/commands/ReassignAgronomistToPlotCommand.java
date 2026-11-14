package com.dymbia.sumaqagro.platform.profiles.domain.model.commands;

/**
 * Command to give an existing assignment to another agronomist. The previous one goes to the history.
 *
 * @since 1.0
 */
public record ReassignAgronomistToPlotCommand(Long assignmentId, Long agronomistUserId) {
    public ReassignAgronomistToPlotCommand {
        if (assignmentId == null || assignmentId <= 0) {
            throw new IllegalArgumentException("Assignment id must be a positive number");
        }
        if (agronomistUserId == null || agronomistUserId <= 0) {
            throw new IllegalArgumentException("Agronomist user id must be a positive number");
        }
    }
}
