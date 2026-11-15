package com.dymbia.sumaqagro.platform.certification.infrastructure.acl;

import com.dymbia.sumaqagro.platform.certification.application.acl.ExternalProfilesService;
import com.dymbia.sumaqagro.platform.profiles.application.queryservices.CooperativeQueryService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetCooperativeMembersByCooperativeIdQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Asks the profiles context whether a member belongs to a cooperative.
 * Certification does not use the cooperative aggregate; it only reads the membership.
 *
 * @since 1.0
 */
@Service
@Transactional(readOnly = true)
public class ExternalProfilesServiceImpl implements ExternalProfilesService {

    private final CooperativeQueryService cooperatives;

    public ExternalProfilesServiceImpl(CooperativeQueryService cooperatives) {
        this.cooperatives = cooperatives;
    }

    @Override
    public boolean memberBelongsToCooperative(Long cooperativeId, Long memberId) {
        if (cooperativeId == null || cooperativeId <= 0 || memberId == null || memberId <= 0) {
            throw new IllegalArgumentException("Cooperative id and member id must be greater than zero");
        }
        return cooperatives.handle(new GetCooperativeMembersByCooperativeIdQuery(cooperativeId)).stream()
                .anyMatch(member -> memberId.equals(member.getId()) && member.isActive());
    }
}
