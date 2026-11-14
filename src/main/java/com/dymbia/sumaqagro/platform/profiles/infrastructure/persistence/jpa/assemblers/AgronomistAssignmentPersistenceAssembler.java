package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.assemblers;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.AgronomistAssignment;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.AssignmentHistoryEntry;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.embeddables.AssignmentHistoryEntryPersistenceEmbeddable;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.entities.AgronomistAssignmentPersistenceEntity;

import java.util.ArrayList;

/**
 * Translates between the {@link AgronomistAssignment} aggregate and
 * {@link AgronomistAssignmentPersistenceEntity}.
 *
 * @since 1.0
 */
public final class AgronomistAssignmentPersistenceAssembler {

    private AgronomistAssignmentPersistenceAssembler() {
    }

    public static AgronomistAssignment toDomainFromPersistence(AgronomistAssignmentPersistenceEntity entity) {
        if (entity == null) return null;
        var history = entity.getHistory().stream()
                .map(item -> new AssignmentHistoryEntry(item.getAgronomistUserId(), item.getAssignedAt(), item.getEndedAt()))
                .toList();
        return AgronomistAssignment.reconstitute(
                entity.getId(),
                entity.getCooperativeId(),
                entity.getAgronomistUserId(),
                entity.getPlotId(),
                entity.getProducerUserId(),
                entity.getAssignedAt(),
                entity.isActive(),
                history);
    }

    public static AgronomistAssignmentPersistenceEntity toPersistenceFromDomain(AgronomistAssignment assignment) {
        var entity = new AgronomistAssignmentPersistenceEntity();
        if (assignment.getId() != null) {
            entity.setId(assignment.getId());
        }
        entity.setCooperativeId(assignment.getCooperativeId());
        entity.setAgronomistUserId(assignment.getAgronomistUserId());
        entity.setPlotId(assignment.getPlotId());
        entity.setProducerUserId(assignment.getProducerUserId());
        entity.setAssignedAt(assignment.getAssignedAt());
        entity.setActive(assignment.isActive());
        var history = new ArrayList<AssignmentHistoryEntryPersistenceEmbeddable>();
        for (var item : assignment.getHistory()) {
            history.add(new AssignmentHistoryEntryPersistenceEmbeddable(
                    item.agronomistUserId(), item.assignedAt(), item.endedAt()));
        }
        entity.setHistory(history);
        return entity;
    }
}
