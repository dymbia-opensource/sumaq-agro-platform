package com.dymbia.sumaqagro.platform.profiles.application.queryservices;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.CooperativeMember;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

/**
 * Read operations of the {@link Cooperative} aggregate.
 *
 * @since 1.0
 */
public interface CooperativeQueryService {
    Optional<Cooperative> handle(GetCooperativeByIdQuery query);

    List<Cooperative> handle(GetCooperativesByManagerUserIdQuery query);

    List<Cooperative> handle(GetAllCooperativesQuery query);

    List<CooperativeMember> handle(GetCooperativeMembersByCooperativeIdQuery query);

    List<CooperativeMember> handle(GetCooperativeMembersByUserIdQuery query);
}
