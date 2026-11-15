package com.dymbia.sumaqagro.platform.profiles.domain.model.queries;

public record GetProfileByIdQuery(Long profileId) {
    public GetProfileByIdQuery {
        if (profileId == null || profileId <= 0) {
            throw new IllegalArgumentException("Profile id must be a positive number");
        }
    }
}
