package com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.profiles.domain.model.events.AgronomistAssignedToPlotEvent;
import com.dymbia.sumaqagro.platform.profiles.domain.model.commands.AssignAgronomistToPlotCommand;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
        import lombok.Getter;

import java.time.Instant;

/**
 * AgronomistAssignment aggregate root.
 * Represents the assignment of an agronomist to a specific plot of a cooperative.
 *
 * @since 1.0
 */
@Getter
@Entity
@Table(name = "agronomist_assignments")
public class AgronomistAssignment extends AuditableAbstractAggregateRoot<AgronomistAssignment> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cooperative_id", nullable = false)
    private Long cooperativeId;

    @Column(name = "agronomist_user_id", nullable = false)
    private Long agronomistUserId;

    @Column(name = "plot_id", nullable = false)
    private Long plotId;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;

    @Column(nullable = false)
    private boolean active;

    /** Required by JPA */
    protected AgronomistAssignment() {}

    public AgronomistAssignment(AssignAgronomistToPlotCommand command) {
        this.cooperativeId = command.cooperativeId();
        this.agronomistUserId = command.agronomistUserId();
        this.plotId = command.plotId();
        this.assignedAt = Instant.now();
        this.active = true;

        registerEvent(new AgronomistAssignedToPlotEvent(
                null,
                this.cooperativeId,
                this.agronomistUserId,
                this.plotId
        ));
    }

    /**
     * Deactivates the current assignment.
     */
    public void deactivate() {
        this.active = false;
    }
}