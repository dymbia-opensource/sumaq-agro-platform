package com.dymbia.sumaqagro.platform.profiles.domain.model.entities;

import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.InvitationStatus;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

/**
 * Agronomist invitation entity (part of the Cooperative aggregate).
 * Stores the invitation sent by email to an agronomist.
 */
@Getter
public class AgronomistInvitation {

    private Long id;
    private Long cooperativeId;
    private String email;
    private String invitationToken;
    private InvitationStatus status;
    private Instant sentAt;

    public AgronomistInvitation(Long id,
                                Long cooperativeId,
                                String email,
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

    /**
     * Reconstitutes an existing invitation from persistence state.
     */
    public static AgronomistInvitation reconstitute(Long id,
                                                    Long cooperativeId,
                                                    String email,
                                                    String invitationToken,
                                                    InvitationStatus status,
                                                    Instant sentAt) {
        return new AgronomistInvitation(id, cooperativeId, email, invitationToken, status, sentAt);
    }

    public void markAsAccepted() {
        this.status = InvitationStatus.ACCEPTED;
    }

    public void markAsExpired() {
        this.status = InvitationStatus.EXPIRED;
    }
}