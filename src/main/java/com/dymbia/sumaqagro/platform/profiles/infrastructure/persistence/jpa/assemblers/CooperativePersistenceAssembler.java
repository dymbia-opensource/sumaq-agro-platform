package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.assemblers;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.AgronomistInvitation;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.CooperativeMember;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.Dni;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PersonName;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.Ruc;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.embeddables.PersonNamePersistenceEmbeddable;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.entities.AgronomistInvitationPersistenceEntity;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.entities.CooperativeMemberPersistenceEntity;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.entities.CooperativePersistenceEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Assembles between the Cooperative domain aggregate and its JPA persistence entity.
 */
public final class CooperativePersistenceAssembler {

    private CooperativePersistenceAssembler() {
    }

    public static CooperativePersistenceEntity toPersistenceFromDomain(Cooperative domain) {
        var entity = new CooperativePersistenceEntity();
        entity.setId(domain.getId());
        entity.setRuc(domain.getRuc().value());
        entity.setBusinessName(domain.getBusinessName());
        entity.setRegion(domain.getRegion());
        entity.setManagerUserId(domain.getManagerUserId());

        List<CooperativeMemberPersistenceEntity> memberEntities = domain.getMembers().stream()
                .map(m -> toMemberPersistence(m, entity))
                .collect(Collectors.toList());
        entity.setMembers(memberEntities);

        List<AgronomistInvitationPersistenceEntity> invitationEntities = domain.getInvitations().stream()
                .map(i -> toInvitationPersistence(i, entity))
                .collect(Collectors.toList());
        entity.setInvitations(invitationEntities);

        return entity;
    }

    public static Cooperative toDomainFromPersistence(CooperativePersistenceEntity entity) {
        List<CooperativeMember> members = entity.getMembers().stream()
                .map(CooperativePersistenceAssembler::toMemberDomain)
                .collect(Collectors.toList());

        List<AgronomistInvitation> invitations = entity.getInvitations().stream()
                .map(CooperativePersistenceAssembler::toInvitationDomain)
                .collect(Collectors.toList());

        return Cooperative.reconstitute(
                entity.getId(),
                new Ruc(entity.getRuc()),
                entity.getBusinessName(),
                entity.getRegion(),
                entity.getManagerUserId(),
                members,
                invitations
        );
    }

    private static CooperativeMemberPersistenceEntity toMemberPersistence(CooperativeMember domain,
                                                                          CooperativePersistenceEntity cooperative) {
        var entity = new CooperativeMemberPersistenceEntity();
        entity.setId(domain.getId());
        entity.setCooperative(cooperative);
        entity.setDni(domain.getDni().value());
        entity.setName(new PersonNamePersistenceEmbeddable(
                domain.getName().firstName(), domain.getName().lastName()));
        entity.setCommunity(domain.getCommunity());
        entity.setJoinedAt(domain.getJoinedAt());
        return entity;
    }

    private static CooperativeMember toMemberDomain(CooperativeMemberPersistenceEntity entity) {
        return CooperativeMember.reconstitute(
                entity.getId(),
                entity.getCooperative().getId(),
                new Dni(entity.getDni()),
                new PersonName(entity.getName().getFirstName(), entity.getName().getLastName()),
                entity.getCommunity(),
                entity.getJoinedAt()
        );
    }

    private static AgronomistInvitationPersistenceEntity toInvitationPersistence(AgronomistInvitation domain,
                                                                                 CooperativePersistenceEntity cooperative) {
        var entity = new AgronomistInvitationPersistenceEntity();
        entity.setId(domain.getId());
        entity.setCooperative(cooperative);
        entity.setEmail(domain.getEmail());
        entity.setInvitationToken(domain.getInvitationToken());
        entity.setStatus(domain.getStatus());
        entity.setSentAt(domain.getSentAt());
        return entity;
    }

    private static AgronomistInvitation toInvitationDomain(AgronomistInvitationPersistenceEntity entity) {
        return AgronomistInvitation.reconstitute(
                entity.getId(),
                entity.getCooperative().getId(),
                entity.getEmail(),
                entity.getInvitationToken(),
                entity.getStatus(),
                entity.getSentAt()
        );
    }
}