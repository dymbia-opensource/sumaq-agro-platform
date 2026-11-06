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
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Cooperative aggregate root.
 * Manages the cooperative data, its member registry and agronomist invitations.
 *
 * @since 1.0
 */
@Getter
@Entity
@Table(name = "cooperatives", uniqueConstraints = {
        @UniqueConstraint(columnNames = "ruc", name = Cooperative.RUC_UNIQUE_CONSTRAINT)
})
public class Cooperative extends AuditableAbstractAggregateRoot<Cooperative> {

    public static final String RUC_UNIQUE_CONSTRAINT = "uk_cooperatives_ruc";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "ruc", nullable = false, length = 11))
    private Ruc ruc;

    @Column(name = "business_name", nullable = false, length = 150)
    private String businessName;

    @Column(nullable = false, length = 100)
    private String region;

    @Column(name = "manager_user_id", nullable = false)
    private Long managerUserId;

    @OneToMany(mappedBy = "cooperative", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CooperativeMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "cooperative", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AgronomistInvitation> invitations = new ArrayList<>();

    /** Required by JPA */
    protected Cooperative() {}

    public Cooperative(RegisterCooperativeCommand command) {
        this.ruc = new Ruc(command.ruc());
        this.businessName = command.businessName().trim();
        this.region = command.region().trim();
        this.managerUserId = command.managerUserId();

        registerEvent(new CooperativeRegisteredEvent(null, this.ruc.value(), this.managerUserId));
    }

    /**
     * Adds a new member to the cooperative padrón.
     *
     * @param command member data
     * @return the created member
     */
    public CooperativeMember addMember(AddCooperativeMemberCommand command) {
        boolean alreadyExists = members.stream()
                .anyMatch(m -> m.getDni().value().equals(command.dni()));
        if (alreadyExists) {
            throw new IllegalArgumentException("A member with this DNI already exists in the cooperative");
        }

        CooperativeMember member = new CooperativeMember(
                this,
                new Dni(command.dni()),
                new PersonName(command.firstName(), command.lastName()),
                command.community(),
                command.joinedAt()
        );
        members.add(member);

        registerEvent(new CooperativeMemberAddedEvent(this.id, member.getDni().value()));
        return member;
    }

    /**
     * Creates and registers an invitation for an agronomist.
     *
     * @param command invitation data
     * @return the created invitation
     */
    public AgronomistInvitation inviteAgronomist(InviteAgronomistCommand command) {
        String token = UUID.randomUUID().toString();

        AgronomistInvitation invitation = new AgronomistInvitation(
                this,
                command.email().trim().toLowerCase(),
                token,
                InvitationStatus.PENDING,
                Instant.now()
        );
        invitations.add(invitation);

        registerEvent(new AgronomistInvitedEvent(this.id, invitation.getEmail(), token));
        return invitation;
    }
}