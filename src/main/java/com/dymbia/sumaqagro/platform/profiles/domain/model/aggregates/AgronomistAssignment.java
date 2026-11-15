package com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AssignAgronomistToPlotCommand;
import com.dymbia.sumaqagro.platform.profiles.domain.model.events.AgronomistAssignedToPlotEvent;
import com.dymbia.sumaqagro.platform.profiles.domain.model.valueobjects.AssignmentHistoryEntry;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * AgronomistAssignment aggregate root: which agronomist attends a plot of a cooperative member.
 *
 * <p>Plain domain class. The table is mapped by {@code AgronomistAssignmentPersistenceEntity}.
 * Only the cooperative director assigns agronomists (US-20), so {@code cooperativeId} is required.
 * {@code producerUserId} is the owner of the plot, read from Field Management.</p>
 *
 * @since 1.0
 */
public class AgronomistAssignment extends AbstractDomainAggregateRoot<AgronomistAssignment> {

    @Getter
    private Long id;

    @Getter
    private Long cooperativeId;

    @Getter
    private Long agronomistUserId;

    @Getter
    private Long plotId;

    @Getter
    private Long producerUserId;

    @Getter
    private Instant assignedAt;

    @Getter
    private boolean active;

    private final List<AssignmentHistoryEntry> history = new ArrayList<>();

    private AgronomistAssignment(Long id,
                                 Long cooperativeId,
                                 Long agronomistUserId,
                                 Long plotId,
                                 Long producerUserId,
                                 Instant assignedAt,
                                 boolean active,
                                 List<AssignmentHistoryEntry> history) {
        this.id = id;
        this.cooperativeId = Objects.requireNonNull(cooperativeId, "cooperativeId must not be null");
        this.agronomistUserId = Objects.requireNonNull(agronomistUserId, "agronomistUserId must not be null");
        this.plotId = Objects.requireNonNull(plotId, "plotId must not be null");
        this.producerUserId = Objects.requireNonNull(producerUserId, "producerUserId must not be null");
        this.assignedAt = Objects.requireNonNull(assignedAt, "assignedAt must not be null");
        this.active = active;
        if (history != null) {
            this.history.addAll(history);
        }
    }

    public static AgronomistAssignment reconstitute(Long id,
                                                    Long cooperativeId,
                                                    Long agronomistUserId,
                                                    Long plotId,
                                                    Long producerUserId,
                                                    Instant assignedAt,
                                                    boolean active,
                                                    List<AssignmentHistoryEntry> history) {
        return new AgronomistAssignment(id, cooperativeId, agronomistUserId, plotId, producerUserId,
                assignedAt, active, history);
    }

    /**
     * Creates a new active assignment. The event is registered after saving, in {@link #onAssigned()}.
     *
     * @param command        the assignment data sent by the director
     * @param producerUserId the owner of the plot, obtained from Field Management
     */
    public AgronomistAssignment(AssignAgronomistToPlotCommand command, Long producerUserId) {
        this(null,
                command.cooperativeId(),
                command.agronomistUserId(),
                command.plotId(),
                producerUserId,
                Instant.now(),
                true,
                null);
    }

    /**
     * Gives the plot to another agronomist. The current one goes to the history.
     * Assigning the same agronomist again changes nothing.
     */
    public AgronomistAssignment reassignTo(Long newAgronomistUserId) {
        Objects.requireNonNull(newAgronomistUserId, "agronomistUserId must not be null");
        if (newAgronomistUserId.equals(this.agronomistUserId) && this.active) {
            return this;
        }
        var now = Instant.now();
        history.add(new AssignmentHistoryEntry(this.agronomistUserId, this.assignedAt, now));
        this.agronomistUserId = newAgronomistUserId;
        this.assignedAt = now;
        this.active = true;
        registerDomainEvent(new AgronomistAssignedToPlotEvent(id, cooperativeId, newAgronomistUserId, plotId));
        return this;
    }

    public void deactivate() {
        this.active = false;
    }

    public List<AssignmentHistoryEntry> getHistory() {
        return Collections.unmodifiableList(history);
    }

    /**
     * Called by the repository after the first save, when the id already exists.
     */
    public void onAssigned() {
        registerDomainEvent(new AgronomistAssignedToPlotEvent(id, cooperativeId, agronomistUserId, plotId));
    }
}
