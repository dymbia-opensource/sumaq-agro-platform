package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.entities;

import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.embeddables.AssignmentHistoryEntryPersistenceEmbeddable;
import com.dymbia.sumaqagro.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA mapping of the {@code AgronomistAssignment} aggregate (table {@code agronomist_assignments}).
 *
 * @since 1.0
 */
@Entity
@Table(name = "agronomist_assignments")
public class AgronomistAssignmentPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "cooperative_id", nullable = false)
    private Long cooperativeId;

    @Column(name = "agronomist_user_id", nullable = false)
    private Long agronomistUserId;

    @Column(name = "plot_id", nullable = false)
    private Long plotId;

    @Column(name = "producer_user_id", nullable = false)
    private Long producerUserId;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;

    @Column(nullable = false)
    private boolean active;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "agronomist_assignment_history",
            joinColumns = @JoinColumn(name = "agronomist_assignment_id"))
    @OrderBy("endedAt ASC")
    private List<AssignmentHistoryEntryPersistenceEmbeddable> history = new ArrayList<>();

    public AgronomistAssignmentPersistenceEntity() {
    }

    public Long getCooperativeId() {
        return cooperativeId;
    }

    public void setCooperativeId(Long cooperativeId) {
        this.cooperativeId = cooperativeId;
    }

    public Long getAgronomistUserId() {
        return agronomistUserId;
    }

    public void setAgronomistUserId(Long agronomistUserId) {
        this.agronomistUserId = agronomistUserId;
    }

    public Long getPlotId() {
        return plotId;
    }

    public void setPlotId(Long plotId) {
        this.plotId = plotId;
    }

    public Long getProducerUserId() {
        return producerUserId;
    }

    public void setProducerUserId(Long producerUserId) {
        this.producerUserId = producerUserId;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Instant assignedAt) {
        this.assignedAt = assignedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<AssignmentHistoryEntryPersistenceEmbeddable> getHistory() {
        return history;
    }

    public void setHistory(List<AssignmentHistoryEntryPersistenceEmbeddable> history) {
        this.history = history;
    }
}
