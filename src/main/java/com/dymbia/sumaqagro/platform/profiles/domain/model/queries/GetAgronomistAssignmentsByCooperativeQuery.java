package com.dymbia.sumaqagro.platform.profiles.domain.model.queries;

/**
 * Query to get all agronomist assignments of a cooperative.
 *
 * @param cooperativeId identity of the cooperative
 * @since 1.0
 */
public record GetAgronomistAssignmentsByCooperativeQuery(Long cooperativeId) {
    public GetAgronomistAssignmentsByCooperativeQuery {
        if (cooperativeId == null || cooperativeId <= 0) {
            throw new IllegalArgumentException("Cooperative id must be a positive number");
        }
    }
}