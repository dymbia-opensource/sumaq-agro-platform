package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.profiles.domain.model.aggregates.Cooperative;
import com.dymbia.sumaqagro.platform.profiles.domain.repositories.CooperativeRepository;
import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.repositories.CooperativePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Adapter that implements the Cooperative repository port with Spring Data JPA.
 *
 * @since 1.0
 */
@Repository
public class CooperativeRepositoryImpl implements CooperativeRepository {

    private final CooperativePersistenceRepository cooperativePersistenceRepository;

    public CooperativeRepositoryImpl(CooperativePersistenceRepository cooperativePersistenceRepository) {
        this.cooperativePersistenceRepository = cooperativePersistenceRepository;
    }

    @Override
    public Optional<Cooperative> findById(Long id) {
        return cooperativePersistenceRepository.findById(id);
    }

    @Override
    public Optional<Cooperative> findByRuc(String ruc) {
        return cooperativePersistenceRepository.findByRuc(ruc);
    }

    @Override
    public boolean existsByRuc(String ruc) {
        return cooperativePersistenceRepository.existsByRuc(ruc);
    }

    @Override
    public Cooperative save(Cooperative cooperative) {
        return cooperativePersistenceRepository.save(cooperative);
    }
}