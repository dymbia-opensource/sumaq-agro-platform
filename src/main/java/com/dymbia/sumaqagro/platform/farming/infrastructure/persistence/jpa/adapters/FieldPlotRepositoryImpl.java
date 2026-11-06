package com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.FieldPlot;
import com.dymbia.sumaqagro.platform.farming.domain.repositories.FieldPlotRepository;
import com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.assemblers.FieldPlotPersistenceAssembler;
import com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.repositories.FieldPlotPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the field plot repository port with Spring Data JPA.
 * It is the only place where the domain aggregate and the JPA entity meet.
 */
@Repository
public class FieldPlotRepositoryImpl implements FieldPlotRepository {
    private final FieldPlotPersistenceRepository fieldPlotPersistenceRepository;

    public FieldPlotRepositoryImpl(FieldPlotPersistenceRepository fieldPlotPersistenceRepository) {
        this.fieldPlotPersistenceRepository = fieldPlotPersistenceRepository;
    }

    @Override
    public Optional<FieldPlot> findById(Long id) {
        return fieldPlotPersistenceRepository.findById(id).map(FieldPlotPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<FieldPlot> findAllByOwnerUserId(Long ownerUserId) {
        return fieldPlotPersistenceRepository.findAllByOwnerUserId(ownerUserId).stream()
                .map(FieldPlotPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public long countByOwnerUserId(Long ownerUserId) {
        return fieldPlotPersistenceRepository.countByOwnerUserId(ownerUserId);
    }

    @Override
    public boolean existsByOwnerUserIdAndName(Long ownerUserId, String name) {
        return fieldPlotPersistenceRepository.existsByOwnerUserIdAndName(ownerUserId, name);
    }

    @Override
    public FieldPlot save(FieldPlot fieldPlot) {
        // saveAndFlush runs the INSERT now, so a violated unique constraint fails here
        var savedEntity = fieldPlotPersistenceRepository.saveAndFlush(FieldPlotPersistenceAssembler.
                toPersistenceFromDomain(fieldPlot));

        return FieldPlotPersistenceAssembler.toDomainFromPersistence(savedEntity);
    }
}
