package com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AddCooperativeMemberCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.InviteAgronomistCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.RegisterCooperativeCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.UpdateCooperativeCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.AgronomistInvitation;
import com.dymbia.sumaqagro.platform.profiles.domain.model.entities.CooperativeMember;
import com.dymbia.sumaqagro.platform.profiles.domain.model.events.AgronomistInvitedEvent;
import com.dymbia.sumaqagro.platform.profiles.domain.model.events.CooperativeMemberAddedEvent;
import com.dymbia.sumaqagro.platform.profiles.domain.model.events.CooperativeRegisteredEvent;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.*;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.util.*;

/**
 * Cooperative aggregate root. Owns its members and its agronomist invitations.
 *
 * <p>No JPA annotation lives here; the tables are mapped by {@code CooperativePersistenceEntity},
 * {@code CooperativeMemberPersistenceEntity} and {@code AgronomistInvitationPersistenceEntity}.</p>
 *
 * @since 1.0
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

    private EmailAddress institutionalEmail;

    @Getter
    private String legalRepresentative;

    @Getter
    private String headquartersAddress;

    private PhoneNumber switchboardPhone;

    private final List<CooperativeMember> members = new ArrayList<>();
    private final List<AgronomistInvitation> invitations = new ArrayList<>();

    public Cooperative(Long id,
                       Ruc ruc,
                       String businessName,
                       String region,
                       Long managerUserId,
                       EmailAddress institutionalEmail,
                       String legalRepresentative,
                       String headquartersAddress,
                       PhoneNumber switchboardPhone,
                       List<CooperativeMember> members,
                       List<AgronomistInvitation> invitations) {
        this.id = id;
        this.ruc = Objects.requireNonNull(ruc, "ruc must not be null");
        this.businessName = requireText(businessName, "businessName");
        this.region = requireText(region, "region");
        this.managerUserId = Objects.requireNonNull(managerUserId, "managerUserId must not be null");
        this.institutionalEmail = institutionalEmail;
        this.legalRepresentative = optionalText(legalRepresentative);
        this.headquartersAddress = optionalText(headquartersAddress);
        this.switchboardPhone = switchboardPhone;
        if (members != null) {
            this.members.addAll(members);
        }
        if (invitations != null) {
            this.invitations.addAll(invitations);
        }
    }

    public static Cooperative reconstitute(Long id,
                                           Ruc ruc,
                                           String businessName,
                                           String region,
                                           Long managerUserId,
                                           EmailAddress institutionalEmail,
                                           String legalRepresentative,
                                           String headquartersAddress,
                                           PhoneNumber switchboardPhone,
                                           List<CooperativeMember> members,
                                           List<AgronomistInvitation> invitations) {
        return new Cooperative(id, ruc, businessName, region, managerUserId, institutionalEmail,
                legalRepresentative, headquartersAddress, switchboardPhone, members, invitations);
    }

    /**
     * Registers a new cooperative (US-17). The four institutional fields are optional here.
     * The {@code CooperativeRegisteredEvent} is registered after saving, in {@link #onRegistered()}.
     */
    public Cooperative(RegisterCooperativeCommand command) {
        this(null,
                new Ruc(command.ruc()),
                command.businessName(),
                command.region(),
                command.managerUserId(),
                toEmailOrNull(command.institutionalEmail()),
                command.legalRepresentative(),
                command.headquartersAddress(),
                toPhoneOrNull(command.switchboardPhone()),
                null,
                null);
    }

    /**
     * Updates the institutional data shown in the cooperative settings view. All values are required.
     */
    public Cooperative updateInstitutionalData(UpdateCooperativeCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        this.ruc = new Ruc(command.ruc());
        this.businessName = requireText(command.businessName(), "businessName");
        this.region = requireText(command.region(), "region");
        this.institutionalEmail = new EmailAddress(command.institutionalEmail());
        this.legalRepresentative = requireText(command.legalRepresentative(), "legalRepresentative");
        this.headquartersAddress = requireText(command.headquartersAddress(), "headquartersAddress");
        this.switchboardPhone = new PhoneNumber(command.switchboardPhone());
        return this;
    }

    public CooperativeMember addMember(AddCooperativeMemberCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        var dni = new Dni(command.dni());
        if (findMemberByDni(dni.value()).isPresent()) {
            throw new IllegalArgumentException("A member with this DNI already exists in the cooperative");
        }
        if (command.userId() != null && hasMemberWithUserId(command.userId())) {
            throw new IllegalArgumentException("This user is already a member of the cooperative");
        }
        var member = new CooperativeMember(
                null,
                this.id,
                dni,
                new PersonName(command.firstName(), command.lastName()),
                command.community(),
                command.joinedAt(),
                true,
                command.userId());
        members.add(member);
        registerDomainEvent(new CooperativeMemberAddedEvent(this.id, member.getDni().value()));
        return member;
    }

    public AgronomistInvitation inviteAgronomist(InviteAgronomistCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        var token = UUID.randomUUID().toString();
        var invitation = new AgronomistInvitation(
                null,
                this.id,
                new EmailAddress(command.email()),
                token,
                InvitationStatus.PENDING,
                Instant.now());
        invitations.add(invitation);
        registerDomainEvent(new AgronomistInvitedEvent(this.id, invitation.getEmail(), token));
        return invitation;
    }

    public Optional<CooperativeMember> findMemberByDni(String dni) {
        if (dni == null) return Optional.empty();
        var value = dni.trim();
        return members.stream().filter(member -> member.getDni().value().equals(value)).findFirst();
    }

    public Optional<AgronomistInvitation> findInvitationByToken(String invitationToken) {
        return invitations.stream()
                .filter(invitation -> invitation.getInvitationToken().equals(invitationToken))
                .findFirst();
    }

    public boolean hasActiveMemberWithDni(String dni) {
        return findMemberByDni(dni).map(CooperativeMember::isActive).orElse(false);
    }

    public boolean hasActiveMemberWithUserId(Long userId) {
        return members.stream().anyMatch(member -> member.isActive() && userId.equals(member.getUserId()));
    }

    private boolean hasMemberWithUserId(Long userId) {
        return members.stream().anyMatch(member -> userId.equals(member.getUserId()));
    }

    public List<CooperativeMember> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public List<AgronomistInvitation> getInvitations() {
        return Collections.unmodifiableList(invitations);
    }

    public String getInstitutionalEmail() {
        return institutionalEmail != null ? institutionalEmail.address() : null;
    }

    public EmailAddress getInstitutionalEmailValue() {
        return institutionalEmail;
    }

    public String getSwitchboardPhone() {
        return switchboardPhone != null ? switchboardPhone.value() : null;
    }

    public PhoneNumber getSwitchboardPhoneValue() {
        return switchboardPhone;
    }

    /**
     * Called by the repository after the first save, when the id already exists.
     */
    public void onRegistered() {
        registerDomainEvent(new CooperativeRegisteredEvent(this.id, this.ruc.value(), this.managerUserId));
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be null or blank");
        }
        return value.trim();
    }

    private static String optionalText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static EmailAddress toEmailOrNull(String value) {
        return value == null || value.isBlank() ? null : new EmailAddress(value);
    }

    private static PhoneNumber toPhoneOrNull(String value) {
        return value == null || value.isBlank() ? null : new PhoneNumber(value);
    }
}
