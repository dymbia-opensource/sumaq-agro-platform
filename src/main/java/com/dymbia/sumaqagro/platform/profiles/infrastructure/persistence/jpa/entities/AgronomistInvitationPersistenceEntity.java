package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.entities;

import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.EmailAddress;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.InvitationStatus;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.converters.EmailAddressPersistenceConverter;
import com.dymbia.sumaqagro.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;

import java.time.Instant;

/**
 * JPA mapping of an agronomist invitation (table {@code agronomist_invitations}).
 *
 * @since 1.0
 */
@Entity
@Table(name = "agronomist_invitations", uniqueConstraints = {
        @UniqueConstraint(columnNames = "invitation_token", name = "uk_agronomist_invitations_token")
})
public class AgronomistInvitationPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cooperative_id", nullable = false)
    private CooperativePersistenceEntity cooperative;

    @Convert(converter = EmailAddressPersistenceConverter.class)
    @Column(nullable = false, length = 150)
    private EmailAddress email;

    @Column(name = "invitation_token", nullable = false, length = 255)
    private String invitationToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvitationStatus status;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    public AgronomistInvitationPersistenceEntity() {
    }

    public CooperativePersistenceEntity getCooperative() {
        return cooperative;
    }

    public void setCooperative(CooperativePersistenceEntity cooperative) {
        this.cooperative = cooperative;
    }

    public EmailAddress getEmail() {
        return email;
    }

    public void setEmail(EmailAddress email) {
        this.email = email;
    }

    public String getInvitationToken() {
        return invitationToken;
    }

    public void setInvitationToken(String invitationToken) {
        this.invitationToken = invitationToken;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }
}