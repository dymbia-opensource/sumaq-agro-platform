package com.dymbia.sumaqagro.platform.certification.interfaces.rest;

import com.dymbia.sumaqagro.platform.certification.application.acl.ExternalFarmingService;
import com.dymbia.sumaqagro.platform.certification.application.acl.ExternalProfilesService;
import com.dymbia.sumaqagro.platform.certification.application.queryservices.HarvestCertificationQueryService;
import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.HarvestBatch;
import com.dymbia.sumaqagro.platform.certification.domain.model.queries.GetHarvestBatchByIdQuery;
import com.dymbia.sumaqagro.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Access rules of the certification endpoints.
 * <p>
 * It reads batches through the query service and cooperatives and plots through the
 * certification ACL, so it never touches repositories or other contexts' internals.
 * </p>
 */
@Component
public class CertificationAccess {
    private final HarvestCertificationQueryService queries;
    private final ExternalProfilesService profiles;
    private final ExternalFarmingService fields;

    public CertificationAccess(HarvestCertificationQueryService queries, ExternalProfilesService profiles,
                               ExternalFarmingService fields) {
        this.queries = queries;
        this.profiles = profiles;
        this.fields = fields;
    }

    boolean directs(UserDetailsImpl principal, Long cooperativeId) {
        return cooperativeId != null && hasRole(principal, "ROLE_COOPERATIVE_DIRECTOR")
                && profiles.managesCooperative(principal.getId(), cooperativeId);
    }

    boolean plotBelongsToMember(Long cooperativeId, Long memberId, Long plotId) {
        if (memberId == null || plotId == null) return false;
        var ownerId = fields.fetchOwnerUserIdByPlotId(plotId);
        return ownerId.isPresent() && profiles.fetchUserIdOfActiveMember(cooperativeId, memberId)
                .filter(ownerId.get()::equals).isPresent();
    }

    boolean managesBatch(UserDetailsImpl principal, Long batchId) {
        return findBatch(batchId).filter(batch -> directs(principal, batch.getCooperativeId())).isPresent();
    }

    boolean canRead(UserDetailsImpl principal, HarvestBatch batch) {
        return directs(principal, batch.getCooperativeId())
                || memberIds(principal, batch.getCooperativeId()).contains(batch.getMemberId());
    }

    boolean canReadBatch(UserDetailsImpl principal, Long batchId) {
        return findBatch(batchId).filter(batch -> canRead(principal, batch)).isPresent();
    }

    List<Long> memberIds(UserDetailsImpl principal, Long cooperativeId) {
        if (!hasRole(principal, "ROLE_FARMER")) return List.of();
        return profiles.fetchActiveMemberIds(principal.getId(), cooperativeId);
    }

    // An invalid id is simply a batch that does not exist
    private Optional<HarvestBatch> findBatch(Long batchId) {
        if (batchId == null || batchId <= 0) return Optional.empty();
        return queries.handle(new GetHarvestBatchByIdQuery(batchId));
    }

    private boolean hasRole(UserDetailsImpl principal, String role) {
        return principal != null
                && principal.getAuthorities().stream().anyMatch(authority -> role.equals(authority.getAuthority()));
    }
}
