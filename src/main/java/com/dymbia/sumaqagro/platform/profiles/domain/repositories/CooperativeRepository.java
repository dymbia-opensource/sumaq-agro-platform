package com.dymbia.sumaqagro.platform.profiles.domain.repositories;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;

import java.util.Optional;

/**
 * Domain repository for the Cooperative aggregate.
 *
 * @since 1.0
 */
public interface CooperativeRepository {

    Cooperative save(Cooperative cooperative);

    Optional<Cooperative> findById(Long id);

    Optional<Cooperative> findByRuc(String ruc);

    boolean existsByRuc(String ruc);
}