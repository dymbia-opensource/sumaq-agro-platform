package com.dymbia.sumaqagro.platform.profiles.domain.model.entities;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.InvitationStatus;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

/**
 * Agronomist invitation entity (part of the Cooperative aggregate).
 * Stores the invitation sent by email to an agronomist.
 *
 * @since 1.0
 */
@Getter
@Entity
@Table(name = "agronomist_invitations", uniqueConstraints = {
        @UniqueConstraint(columnNames = "invitation_token", name = AgronomistInvitation.TOKEN_UNIQUE_CONSTRAINT)
})
public class AgronomistInvitation extends AuditableAbstractAggregateRoot<AgronomistInvitation> {

    public static final String TOKEN_UNIQUE_CONSTRAINT = "uk_agronomist_invitations_token";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cooperative_id", nullable = false)
    private Cooperative cooperative;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(name = "invitation_token", nullable = false, length = 255)
    private String invitationToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvitationStatus status;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    /** Required by JPA */
    protected AgronomistInvitation() {}

    public AgronomistInvitation(Cooperative cooperative,
                                String email,
                                String invitationToken,
                                InvitationStatus status,
                                Instant sentAt) {
        this.cooperative = cooperative;
        this.email = email;
        this.invitationToken = invitationToken;
        this.status = status;
        this.sentAt = sentAt;
    }

    public void markAsAccepted() {
        this.status = InvitationStatus.ACCEPTED;
    }

    public void markAsExpired() {
        this.status = InvitationStatus.EXPIRED;
    }
}