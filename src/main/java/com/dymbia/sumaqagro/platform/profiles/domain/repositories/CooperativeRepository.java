package com.dymbia.sumaqagro.platform.profiles.domain.repositories;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;

import java.util.Optional;

/**
 * Repository port of the Cooperative aggregate.
 * The domain says what it needs and the infrastructure layer implements it.
 *
 * @since 1.0
 */
public interface CooperativeRepository {

    /**
     * Finds a cooperative by its id.
     *
     * @param id the cooperative id
     * @return the cooperative, or an empty optional
     */
    Optional<Cooperative> findById(Long id);

    /**
     * Finds a cooperative by its RUC.
     *
     * @param ruc the RUC
     * @return the cooperative, or an empty optional
     */
    Optional<Cooperative> findByRuc(String ruc);

    /**
     * Checks whether a cooperative with the given RUC already exists.
     *
     * @param ruc the RUC
     * @return true if a cooperative with that RUC exists
     */
    boolean existsByRuc(String ruc);

    /**
     * Saves the cooperative.
     *
     * @param cooperative the cooperative to save
     * @return the saved cooperative, with its id
     */
    Cooperative save(Cooperative cooperative);
}