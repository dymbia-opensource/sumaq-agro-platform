package com.dymbia.sumaqagro.platform.profiles.domain.model.queries;

public record GetAgronomistAssignmentsByAgronomistUserIdQuery(Long agronomistUserId) {
    public GetAgronomistAssignmentsByAgronomistUserIdQuery {
        if (agronomistUserId == null || agronomistUserId <= 0) {
            throw new IllegalArgumentException("Agronomist user id must be a positive number");
        }
    }
}
