package com.dymbia.sumaqagro.platform.certification.domain.model.queries;

import java.util.List;

/**
 * Query for the harvest batches that some members delivered to a cooperative.
 *
 * @param cooperativeId the cooperative that received the batches
 * @param memberIds     the delivering members
 * @since 1.1
 */
public record GetAllHarvestBatchesByCooperativeIdAndMemberIdsQuery(Long cooperativeId, List<Long> memberIds) {

    public GetAllHarvestBatchesByCooperativeIdAndMemberIdsQuery {
        if (cooperativeId == null || cooperativeId <= 0) {
            throw new IllegalArgumentException("Cooperative id must be greater than zero");
        }
        memberIds = memberIds == null ? List.of() : List.copyOf(memberIds);
    }
}
