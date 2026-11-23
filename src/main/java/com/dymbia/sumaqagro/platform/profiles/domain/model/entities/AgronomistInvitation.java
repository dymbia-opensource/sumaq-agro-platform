package com.dymbia.sumaqagro.platform.profiles.domain.model.entities;

import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.EmailAddress;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.InvitationStatus;

import java.time.Instant;
import java.util.Objects;

/**
 * Invitation sent by a cooperative to an agronomist (entity inside the {@code Cooperative} aggregate).
 *
 * <p>Once the agronomist has an account with the invited email, the invitation is accepted and keeps
 * the IAM user id of the agronomist: the accepted invitations are the technical team of the cooperative.</p>
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
    private Long agronomistUserId;

    public AgronomistInvitation(Long id,
                                Long cooperativeId,
                                EmailAddress email,
                                String invitationToken,
                                InvitationStatus status,
                                Instant sentAt) {
        this(id, cooperativeId, email, invitationToken, status, sentAt, null);
    }

    public AgronomistInvitation(Long id,
                                Long cooperativeId,
                                EmailAddress email,
                                String invitationToken,
                                InvitationStatus status,
                                Instant sentAt,
                                Long agronomistUserId) {
        this.id = id;
        this.cooperativeId = cooperativeId;
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.invitationToken = Objects.requireNonNull(invitationToken, "invitationToken must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.sentAt = Objects.requireNonNull(sentAt, "sentAt must not be null");
        if (agronomistUserId != null && agronomistUserId <= 0) {
            throw new IllegalArgumentException("Agronomist user id must be a positive number");
        }
        this.agronomistUserId = agronomistUserId;
    }

    public static AgronomistInvitation reconstitute(Long id,
                                                    Long cooperativeId,
                                                    EmailAddress email,
                                                    String invitationToken,
                                                    InvitationStatus status,
                                                    Instant sentAt,
                                                    Long agronomistUserId) {
        return new AgronomistInvitation(id, cooperativeId, email, invitationToken, status, sentAt, agronomistUserId);
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

    /** IAM user id of the agronomist who accepted the invitation, or {@code null} while it is pending. */
    public Long getAgronomistUserId() {
        return agronomistUserId;
    }

    public boolean isPending() {
        return status == InvitationStatus.PENDING;
    }

    /** Whether this invitation still counts for the email: pending or accepted. */
    public boolean isActive() {
        return status != InvitationStatus.EXPIRED;
    }

    /**
     * Accepts the invitation for the agronomist account that uses the invited email.
     *
     * @param agronomistUserId IAM user id of the agronomist
     */
    public void accept(Long agronomistUserId) {
        if (agronomistUserId == null || agronomistUserId <= 0) {
            throw new IllegalArgumentException("Agronomist user id must be a positive number");
        }
        if (!isPending()) {
            throw new IllegalStateException("Only a pending invitation can be accepted");
        }
        this.status = InvitationStatus.ACCEPTED;
        this.agronomistUserId = agronomistUserId;
    }

    public void markAsExpired() {
        this.status = InvitationStatus.EXPIRED;
    }
}
