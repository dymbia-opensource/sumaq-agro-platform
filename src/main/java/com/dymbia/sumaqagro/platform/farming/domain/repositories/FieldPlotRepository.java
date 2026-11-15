package com.dymbia.sumaqagro.platform.farming.domain.repositories;

import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.FieldPlot;

import java.util.List;
import java.util.Optional;

/**
 * Repository port of the field plot aggregate.
 * The domain says what it needs and the infrastructure layer implements it.
 */
public interface FieldPlotRepository {

    /**
     * Finds a field plot by its id.
     *
     * @param id the plot id
     * @return the plot, or an empty optional
     */
    Optional<FieldPlot> findById(Long id);

    /**
     * Finds all the plots of a producer.
     *
     * @param ownerUserId the producer id
     * @return the plots of the producer, empty if there are none
     */
    List<FieldPlot> findAllByOwnerUserId(Long ownerUserId);

    /**
     * Counts the plots of a producer. It is used to check the plan quota.
     *
     * @param ownerUserId the producer id
     * @return how many plots the producer has
     */
    long countByOwnerUserId(Long ownerUserId);

    long countByCooperativeId(Long cooperativeId);

    /**
     * Checks whether a producer already has a plot with that name.
     *
     * @param ownerUserId the producer id
     * @param name        the plot name
     * @return true if the name is already used by that producer
     */
    boolean existsByOwnerUserIdAndName(Long ownerUserId, String name);

    /**
     * Saves the field plot and runs the INSERT or UPDATE right away, so a violated
     * unique constraint fails here and not at the end of the transaction.
     *
     * @param fieldPlot the plot to save
     * @return the saved plot, with its id
     */
    FieldPlot save(FieldPlot fieldPlot);
}
