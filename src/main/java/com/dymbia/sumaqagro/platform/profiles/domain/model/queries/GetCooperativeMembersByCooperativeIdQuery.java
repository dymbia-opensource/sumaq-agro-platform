package com.dymbia.sumaqagro.platform.profiles.domain.model.queries;

public record GetCooperativeMembersByCooperativeIdQuery(Long cooperativeId) {
    public GetCooperativeMembersByCooperativeIdQuery {
        if (cooperativeId == null || cooperativeId <= 0) {
            throw new IllegalArgumentException("Cooperative id must be a positive number");
        }
    }
}
