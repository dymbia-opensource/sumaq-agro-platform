package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.Instant;

/**
 * One row of the table {@code agronomist_assignment_history}.
 *
 * @since 1.0
 */
@Embeddable
public class AssignmentHistoryEntryPersistenceEmbeddable {

    @Column(name = "agronomist_user_id", nullable = false)
    private Long agronomistUserId;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;

    @Column(name = "ended_at", nullable = false)
    private Instant endedAt;

    public AssignmentHistoryEntryPersistenceEmbeddable() {
    }

    public AssignmentHistoryEntryPersistenceEmbeddable(Long agronomistUserId, Instant assignedAt, Instant endedAt) {
        this.agronomistUserId = agronomistUserId;
        this.assignedAt = assignedAt;
        this.endedAt = endedAt;
    }

    public Long getAgronomistUserId() {
        return agronomistUserId;
    }

    public void setAgronomistUserId(Long agronomistUserId) {
        this.agronomistUserId = agronomistUserId;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Instant assignedAt) {
        this.assignedAt = assignedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Instant endedAt) {
        this.endedAt = endedAt;
    }
}
