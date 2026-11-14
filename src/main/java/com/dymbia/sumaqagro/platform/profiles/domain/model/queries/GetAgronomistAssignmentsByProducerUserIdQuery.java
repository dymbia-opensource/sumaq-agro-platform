package com.dymbia.sumaqagro.platform.profiles.domain.model.queries;

public record GetAgronomistAssignmentsByProducerUserIdQuery(Long producerUserId) {
    public GetAgronomistAssignmentsByProducerUserIdQuery {
        if (producerUserId == null || producerUserId <= 0) {
            throw new IllegalArgumentException("Producer user id must be a positive number");
        }
    }
}
