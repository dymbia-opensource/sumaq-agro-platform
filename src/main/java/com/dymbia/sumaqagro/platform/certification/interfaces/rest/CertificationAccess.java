package com.dymbia.sumaqagro.platform.certification.interfaces.rest;

import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.HarvestBatch;
import com.dymbia.sumaqagro.platform.certification.domain.repositories.HarvestBatchRepository;
import com.dymbia.sumaqagro.platform.farming.interfaces.acl.FieldManagementContextFacade;
import com.dymbia.sumaqagro.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.dymbia.sumaqagro.platform.profiles.application.queryservices.CooperativeQueryService;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.CooperativeMember;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetCooperativeByIdQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetCooperativeMembersByUserIdQuery;
import com.dymbia.sumaqagro.platform.profiles.domain.model.queries.GetCooperativeMembersByCooperativeIdQuery;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CertificationAccess {
    private final HarvestBatchRepository batches;
    private final CooperativeQueryService cooperatives;
    private final FieldManagementContextFacade fields;

    public CertificationAccess(HarvestBatchRepository batches, CooperativeQueryService cooperatives,
                               FieldManagementContextFacade fields) {
        this.batches = batches;
        this.cooperatives = cooperatives;
        this.fields = fields;
    }

    boolean directs(UserDetailsImpl principal, Long cooperativeId) {
        return cooperativeId != null && hasRole(principal, "ROLE_COOPERATIVE_DIRECTOR")
                && cooperatives.handle(new GetCooperativeByIdQuery(cooperativeId))
                .filter(cooperative -> principal.getId().equals(cooperative.getManagerUserId())).isPresent();
    }

    boolean plotBelongsToMember(Long cooperativeId, Long memberId, Long plotId) {
        if (memberId == null || plotId == null) return false;
        var ownerId = fields.fetchOwnerUserIdByFieldPlotId(plotId);
        return ownerId != null && ownerId > 0 && cooperatives
                .handle(new GetCooperativeMembersByCooperativeIdQuery(cooperativeId)).stream()
                .anyMatch(member -> memberId.equals(member.getId())
                        && member.isActive() && ownerId.equals(member.getUserId()));
    }

    boolean managesBatch(UserDetailsImpl principal, Long batchId) {
        return batchId != null && batches.findById(batchId)
                .filter(batch -> directs(principal, batch.getCooperativeId())).isPresent();
    }

    boolean canRead(UserDetailsImpl principal, HarvestBatch batch) {
        return directs(principal, batch.getCooperativeId())
                || memberIds(principal, batch.getCooperativeId()).contains(batch.getMemberId());
    }

    boolean canReadBatch(UserDetailsImpl principal, Long batchId) {
        return batches.findById(batchId).filter(batch -> canRead(principal, batch)).isPresent();
    }

    List<Long> memberIds(UserDetailsImpl principal, Long cooperativeId) {
        if (!hasRole(principal, "ROLE_FARMER")) return List.of();
        return cooperatives.handle(new GetCooperativeMembersByUserIdQuery(principal.getId())).stream()
                .filter(member -> member.isActive() && cooperativeId.equals(member.getCooperativeId()))
                .map(CooperativeMember::getId).toList();
    }

    private boolean hasRole(UserDetailsImpl principal, String role) {
        return principal != null
                && principal.getAuthorities().stream().anyMatch(authority -> role.equals(authority.getAuthority()));
    }
}
