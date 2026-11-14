package com.dymbia.sumaqagro.platform.profiles.domain.model.queries;

public record GetCooperativeByIdQuery(Long cooperativeId) {
    public GetCooperativeByIdQuery {
        if (cooperativeId == null || cooperativeId <= 0) {
            throw new IllegalArgumentException("Cooperative id must be a positive number");
        }
    }
}
