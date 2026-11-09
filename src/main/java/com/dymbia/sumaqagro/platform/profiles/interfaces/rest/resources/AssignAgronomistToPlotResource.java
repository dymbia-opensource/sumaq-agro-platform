package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources;

/**
 * Request resource to assign an agronomist to a plot.
 *
 * @since 1.0
 */
public record AssignAgronomistToPlotResource(
        Long cooperativeId,
        Long agronomistUserId,
        Long plotId
) {
}
