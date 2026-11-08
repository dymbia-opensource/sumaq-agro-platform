package com.dymbia.sumaqagro.platform.profiles.domain.model.events;

/**
 * Domain event raised when an agronomist is assigned to a plot.
 *
 * @param assignmentId     identity of the assignment (could be null before persistence)
 * @param cooperativeId    identity of the cooperative
 * @param agronomistUserId identity of the agronomist
 * @param plotId           identity of the plot
 * @since 1.0
 */
public record AgronomistAssignedToPlotEvent(
        Long assignmentId,
        Long cooperativeId,
        Long agronomistUserId,
        Long plotId
) {
}