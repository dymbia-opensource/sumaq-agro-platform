package com.dymbia.sumaqagro.platform.profiles.domain.repositories;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.AgronomistAssignment;

import java.util.List;
import java.util.Optional;

/**
 * Repository port of the AgronomistAssignment aggregate.
 * The domain says what it needs and the infrastructure layer implements it.
 *
 * @since 1.0
 */
public interface AgronomistAssignmentRepository {

    /**
     * Finds an assignment by its id.
     *
     * @param id the assignment id
     * @return the assignment, or an empty optional
     */
    Optional<AgronomistAssignment> findById(Long id);

    /**
     * Finds the active assignment of a plot.
     *
     * @param plotId the plot id
     * @return the active assignment, or an empty optional
     */
    Optional<AgronomistAssignment> findActiveByPlotId(Long plotId);

    /**
     * Finds all assignments of a cooperative.
     *
     * @param cooperativeId the cooperative id
     * @return the list of assignments
     */
    List<AgronomistAssignment> findAllByCooperativeId(Long cooperativeId);

    /**
     * Checks whether a plot already has an active assignment.
     *
     * @param plotId the plot id
     * @return true if there is an active assignment for that plot
     */
    boolean existsActiveByPlotId(Long plotId);

    /**
     * Saves the assignment.
     *
     * @param assignment the assignment to save
     * @return the saved assignment, with its id
     */
    AgronomistAssignment save(AgronomistAssignment assignment);
}