package com.dymbia.sumaqagro.platform.profiles.domain.model.entities;

import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.EmailAddress;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.InvitationStatus;

import java.time.Instant;
import java.util.Objects;

/**
 * Invitation sent by a cooperative to an agronomist (entity inside the {@code Cooperative} aggregate).
 *
 * @since 1.0
 */
public class AgronomistInvitation {
    private Long id;
    private Long cooperativeId;
    private EmailAddress email;
    private String invitationToken;
    private InvitationStatus status;
    private Instant sentAt;

    public AgronomistInvitation(Long id,
                                Long cooperativeId,
                                EmailAddress email,
                                String invitationToken,
                                InvitationStatus status,
                                Instant sentAt) {
        this.id = id;
        this.cooperativeId = cooperativeId;
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.invitationToken = Objects.requireNonNull(invitationToken, "invitationToken must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.sentAt = Objects.requireNonNull(sentAt, "sentAt must not be null");
    }

    public static AgronomistInvitation reconstitute(Long id,
                                                    Long cooperativeId,
                                                    EmailAddress email,
                                                    String invitationToken,
                                                    InvitationStatus status,
                                                    Instant sentAt) {
        return new AgronomistInvitation(id, cooperativeId, email, invitationToken, status, sentAt);
    }

    public Long getId() {
        return id;
    }

    public Long getCooperativeId() {
        return cooperativeId;
    }

    public String getEmail() {
        return email.address();
    }

    public EmailAddress getEmailValue() {
        return email;
    }

    public String getInvitationToken() {
        return invitationToken;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void markAsAccepted() {
        this.status = InvitationStatus.ACCEPTED;
    }

    public void markAsExpired() {
        this.status = InvitationStatus.EXPIRED;
    }
}
