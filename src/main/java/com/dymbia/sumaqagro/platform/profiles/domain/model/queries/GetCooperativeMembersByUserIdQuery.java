package com.dymbia.sumaqagro.platform.profiles.domain.model.queries;

public record GetCooperativeMembersByUserIdQuery(Long userId) {
    public GetCooperativeMembersByUserIdQuery {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User id must be a positive number");
        }
    }
}
