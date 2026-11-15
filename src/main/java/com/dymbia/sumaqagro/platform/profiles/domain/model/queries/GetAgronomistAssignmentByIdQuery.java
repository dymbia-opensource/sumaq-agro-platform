package com.dymbia.sumaqagro.platform.profiles.domain.model.queries;

public record GetAgronomistAssignmentByIdQuery(Long assignmentId) {
    public GetAgronomistAssignmentByIdQuery {
        if (assignmentId == null || assignmentId <= 0) {
            throw new IllegalArgumentException("Assignment id must be a positive number");
        }
    }
}
