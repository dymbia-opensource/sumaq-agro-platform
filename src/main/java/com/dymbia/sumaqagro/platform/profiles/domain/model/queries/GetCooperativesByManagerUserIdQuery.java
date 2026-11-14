package com.dymbia.sumaqagro.platform.profiles.domain.model.queries;

public record GetCooperativesByManagerUserIdQuery(Long managerUserId) {
    public GetCooperativesByManagerUserIdQuery {
        if (managerUserId == null || managerUserId <= 0) {
            throw new IllegalArgumentException("Manager user id must be a positive number");
        }
    }
}
