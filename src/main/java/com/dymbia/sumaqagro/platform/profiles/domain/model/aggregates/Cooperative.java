package com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AddCooperativeMemberCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.InviteAgronomistCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.RegisterCooperativeCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.AgronomistInvitation;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.CooperativeMember;
import com.dymbia.sumaqagro.platform.profiles.domain.model.events.AgronomistInvitedEvent;
import com.dymbia.sumaqagro.platform.profiles.domain.model.events.CooperativeMemberAddedEvent;
import com.dymbia.sumaqagro.platform.profiles.domain.model.events.CooperativeRegisteredEvent;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.Dni;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.InvitationStatus;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.PersonName;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.Ruc;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Cooperative aggregate root.
 *
 * <p>Extends {@link AbstractDomainAggregateRoot} to gain domain event registration
 * support. No JPA or persistence annotation is present here — those concerns live
 * exclusively in {@code CooperativePersistenceEntity}.</p>
 *
 * <p>Manages the cooperative data, its member registry (padrón) and agronomist invitations.</p>
 */
public class Cooperative extends AbstractDomainAggregateRoot<Cooperative> {

    @Getter
    private Long id;

    @Getter
    private Ruc ruc;

    @Getter
    private String businessName;

    @Getter
    private String region;

    @Getter
    private Long managerUserId;

    private final List<CooperativeMember> members = new ArrayList<>();
    private final List<AgronomistInvitation> invitations = new ArrayList<>();

    /**
     * Creates a cooperative from the provided domain values.
     */
    public Cooperative(Long id,
                       Ruc ruc,
                       String businessName,
                       String region,
                       Long managerUserId,
                       List<CooperativeMember> members,
                       List<AgronomistInvitation> invitations) {
        this.id = id;
        this.ruc = Objects.requireNonNull(ruc, "ruc must not be null");
        this.businessName = Objects.requireNonNull(businessName, "businessName must not be null").trim();
        this.region = Objects.requireNonNull(region, "region must not be null").trim();
        this.managerUserId = Objects.requireNonNull(managerUserId, "managerUserId must not be null");

        if (members != null) {
            this.members.addAll(members);
        }
        if (invitations != null) {
            this.invitations.addAll(invitations);
        }
    }

    /**
     * Reconstitutes an existing cooperative from persistence state.
     */
    public static Cooperative reconstitute(Long id,
                                           Ruc ruc,
                                           String businessName,
                                           String region,
                                           Long managerUserId,
                                           List<CooperativeMember> members,
                                           List<AgronomistInvitation> invitations) {
        return new Cooperative(id, ruc, businessName, region, managerUserId, members, invitations);
    }

    /**
     * Creates a new cooperative from the register command (no identity assigned yet).
     *
     * @param command the register cooperative command
     */
    public Cooperative(RegisterCooperativeCommand command) {
        this(null,
                new Ruc(command.ruc()),
                command.businessName(),
                command.region(),
                command.managerUserId(),
                null,
                null);

        registerDomainEvent(new CooperativeRegisteredEvent(null, this.ruc.value(), this.managerUserId));
    }

    /**
     * Adds a new member to the cooperative padrón.
     *
     * @param command the member data
     * @return the created member
     */
    public CooperativeMember addMember(AddCooperativeMemberCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        boolean alreadyExists = members.stream()
                .anyMatch(m -> m.getDni().value().equals(command.dni()));
        if (alreadyExists) {
            throw new IllegalArgumentException("A member with this DNI already exists in the cooperative");
        }

        CooperativeMember member = new CooperativeMember(
                null,
                this.id,
                new Dni(command.dni()),
                new PersonName(command.firstName(), command.lastName()),
                command.community(),
                command.joinedAt()
        );
        members.add(member);

        registerDomainEvent(new CooperativeMemberAddedEvent(this.id, member.getDni().value()));
        return member;
    }

    /**
     * Creates and registers an invitation for an agronomist.
     *
     * @param command the invitation data
     * @return the created invitation
     */
    public AgronomistInvitation inviteAgronomist(InviteAgronomistCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        String token = UUID.randomUUID().toString();

        AgronomistInvitation invitation = new AgronomistInvitation(
                null,
                this.id,
                command.email().trim().toLowerCase(),
                token,
                InvitationStatus.PENDING,
                Instant.now()
        );
        invitations.add(invitation);

        registerDomainEvent(new AgronomistInvitedEvent(this.id, invitation.getEmail(), token));
        return invitation;
    }

    /**
     * Returns an unmodifiable view of the members.
     */
    public List<CooperativeMember> getMembers() {
        return Collections.unmodifiableList(members);
    }

    /**
     * Returns an unmodifiable view of the invitations.
     */
    public List<AgronomistInvitation> getInvitations() {
        return Collections.unmodifiableList(invitations);
    }

    /**
     * Signals that this cooperative has just been registered and persisted.
     *
     * <p>Called by the repository adapter after the JPA identity has been assigned.</p>
     */
    public void onRegistered() {
        registerDomainEvent(new CooperativeRegisteredEvent(this.id, this.ruc.value(), this.managerUserId));
    }
}