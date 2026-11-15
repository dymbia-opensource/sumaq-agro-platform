package com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.crop.domain.model.aggregates.TechnicalPrescription;
import com.dymbia.sumaqagro.platform.crop.domain.repositories.TechnicalPrescriptionRepository;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.assemblers.TechnicalPrescriptionPersistenceAssembler;
import com.dymbia.sumaqagro.platform.crop.infrastructure.persistence.jpa.repositories.TechnicalPrescriptionPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Implements the technical prescription domain repository using Spring Data JPA.
 */
@Repository
public class TechnicalPrescriptionRepositoryImpl implements TechnicalPrescriptionRepository {

    private final TechnicalPrescriptionPersistenceRepository persistenceRepository;

    public TechnicalPrescriptionRepositoryImpl(TechnicalPrescriptionPersistenceRepository persistenceRepository) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override
    public TechnicalPrescription save(TechnicalPrescription prescription) {
        var entity = TechnicalPrescriptionPersistenceAssembler
                .toPersistenceFromDomain(prescription);

        var savedEntity = persistenceRepository.save(entity);

        return TechnicalPrescriptionPersistenceAssembler
                .toDomainFromPersistence(savedEntity);
    }

    @Override
    public Optional<TechnicalPrescription> findById(Long technicalPrescriptionId) {
        return persistenceRepository.findById(technicalPrescriptionId)
                .map(TechnicalPrescriptionPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<TechnicalPrescription> findAllByPlotId(Long plotId) {
        return persistenceRepository.findAllByPlotId(plotId)
                .stream()
                .map(TechnicalPrescriptionPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public boolean existsByPestReportId(Long pestReportId) {
        return persistenceRepository.existsByPestReportId(pestReportId);
    }
}