package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.assemblers;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.AgronomistInvitation;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.CooperativeMember;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.Dni;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PersonName;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PhoneNumber;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.Ruc;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.embeddables.PersonNamePersistenceEmbeddable;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.entities.AgronomistInvitationPersistenceEntity;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.entities.CooperativeMemberPersistenceEntity;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.entities.CooperativePersistenceEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Translates between the {@link Cooperative} aggregate (with its members and invitations)
 * and {@link CooperativePersistenceEntity}.
 *
 * @since 1.0
 */
public final class CooperativePersistenceAssembler {

    private CooperativePersistenceAssembler() {
    }

    public static Cooperative toDomainFromPersistence(CooperativePersistenceEntity entity) {
        if (entity == null) return null;
        List<CooperativeMember> members = entity.getMembers().stream()
                .map(CooperativePersistenceAssembler::toMemberDomain)
                .toList();
        List<AgronomistInvitation> invitations = entity.getInvitations().stream()
                .map(CooperativePersistenceAssembler::toInvitationDomain)
                .toList();
        return Cooperative.reconstitute(
                entity.getId(),
                new Ruc(entity.getRuc()),
                entity.getBusinessName(),
                entity.getRegion(),
                entity.getManagerUserId(),
                entity.getInstitutionalEmail(),
                entity.getLegalRepresentative(),
                entity.getHeadquartersAddress(),
                entity.getSwitchboardPhone() != null ? new PhoneNumber(entity.getSwitchboardPhone()) : null,
                members,
                invitations);
    }

    public static CooperativePersistenceEntity toPersistenceFromDomain(Cooperative cooperative) {
        var entity = new CooperativePersistenceEntity();
        if (cooperative.getId() != null) {
            entity.setId(cooperative.getId());
        }
        entity.setRuc(cooperative.getRuc().value());
        entity.setBusinessName(cooperative.getBusinessName());
        entity.setRegion(cooperative.getRegion());
        entity.setManagerUserId(cooperative.getManagerUserId());
        entity.setInstitutionalEmail(cooperative.getInstitutionalEmailValue());
        entity.setLegalRepresentative(cooperative.getLegalRepresentative());
        entity.setHeadquartersAddress(cooperative.getHeadquartersAddress());
        entity.setSwitchboardPhone(cooperative.getSwitchboardPhone());

        var memberEntities = new ArrayList<CooperativeMemberPersistenceEntity>();
        for (var member : cooperative.getMembers()) {
            memberEntities.add(toMemberPersistence(member, entity));
        }
        entity.setMembers(memberEntities);

        var invitationEntities = new ArrayList<AgronomistInvitationPersistenceEntity>();
        for (var invitation : cooperative.getInvitations()) {
            invitationEntities.add(toInvitationPersistence(invitation, entity));
        }
        entity.setInvitations(invitationEntities);
        return entity;
    }

    private static CooperativeMemberPersistenceEntity toMemberPersistence(CooperativeMember member,
                                                                          CooperativePersistenceEntity cooperative) {
        var entity = new CooperativeMemberPersistenceEntity();
        if (member.getId() != null) {
            entity.setId(member.getId());
        }
        entity.setCooperative(cooperative);
        entity.setDni(member.getDni().value());
        entity.setName(new PersonNamePersistenceEmbeddable(
                member.getName().firstName(), member.getName().lastName()));
        entity.setCommunity(member.getCommunity());
        entity.setJoinedAt(member.getJoinedAt());
        entity.setActive(member.isActive());
        entity.setUserId(member.getUserId());
        return entity;
    }

    private static CooperativeMember toMemberDomain(CooperativeMemberPersistenceEntity entity) {
        return CooperativeMember.reconstitute(
                entity.getId(),
                entity.getCooperative().getId(),
                new Dni(entity.getDni()),
                new PersonName(entity.getName().getFirstName(), entity.getName().getLastName()),
                entity.getCommunity(),
                entity.getJoinedAt(),
                entity.isActive(),
                entity.getUserId());
    }

    private static AgronomistInvitationPersistenceEntity toInvitationPersistence(AgronomistInvitation invitation,
                                                                                 CooperativePersistenceEntity cooperative) {
        var entity = new AgronomistInvitationPersistenceEntity();
        if (invitation.getId() != null) {
            entity.setId(invitation.getId());
        }
        entity.setCooperative(cooperative);
        entity.setEmail(invitation.getEmailValue());
        entity.setInvitationToken(invitation.getInvitationToken());
        entity.setStatus(invitation.getStatus());
        entity.setSentAt(invitation.getSentAt());
        return entity;
    }

    private static AgronomistInvitation toInvitationDomain(AgronomistInvitationPersistenceEntity entity) {
        return AgronomistInvitation.reconstitute(
                entity.getId(),
                entity.getCooperative().getId(),
                entity.getEmail(),
                entity.getInvitationToken(),
                entity.getStatus(),
                entity.getSentAt());
    }
}
