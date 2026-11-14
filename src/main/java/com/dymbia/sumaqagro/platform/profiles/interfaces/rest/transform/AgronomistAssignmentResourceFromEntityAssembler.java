package com.dymbia.sumaqagro.platform.profiles.interfaces.rest.transform;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.AgronomistAssignment;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.AgronomistAssignmentResource;
import com.dymbia.sumaqagro.platform.profiles.interfaces.rest.resources.AssignmentHistoryEntryResource;

public final class AgronomistAssignmentResourceFromEntityAssembler {

    private AgronomistAssignmentResourceFromEntityAssembler() {
    }

    public static AgronomistAssignmentResource toResourceFromEntity(AgronomistAssignment entity) {
        var history = entity.getHistory().stream()
                .map(item -> new AssignmentHistoryEntryResource(item.agronomistUserId(), item.assignedAt(), item.endedAt()))
                .toList();
        return new AgronomistAssignmentResource(
                entity.getId(),
                entity.getCooperativeId(),
                entity.getAgronomistUserId(),
                entity.getPlotId(),
                entity.getProducerUserId(),
                entity.getAssignedAt(),
                entity.isActive(),
                entity.isActive() ? "ACTIVE" : "INACTIVE",
                history);
    }
}
