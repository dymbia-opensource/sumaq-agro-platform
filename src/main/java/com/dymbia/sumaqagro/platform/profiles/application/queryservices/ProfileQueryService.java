package com.dymbia.sumaqagro.platform.profiles.application.queryservices;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Profile;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetAllProfilesQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetProfileByIdQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetProfileByUserIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Read operations of the {@link Profile} aggregate.
 *
 * @since 1.0
 */
public interface ProfileQueryService {
    Optional<Profile> handle(GetProfileByIdQuery query);

    Optional<Profile> handle(GetProfileByUserIdQuery query);

    List<Profile> handle(GetAllProfilesQuery query);
}
