package com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.adapters;

import com.dymbia.sumaqagro.platform.subscriptions.domain.model.entities.Plan;
import com.dymbia.sumaqagro.platform.subscriptions.domain.model.valueobjects.PlanCode;
import com.dymbia.sumaqagro.platform.subscriptions.domain.repositories.PlanRepository;
import com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.assemblers.PlanPersistenceAssembler;
import com.dymbia.sumaqagro.platform.subscriptions.infrastructure.persistence.jpa.repositories.PlanPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Bridges the plan repository port and Spring Data JPA. */
@Repository
public class PlanRepositoryImpl implements PlanRepository {

    private final PlanPersistenceRepository planPersistenceRepository;

    public PlanRepositoryImpl(PlanPersistenceRepository planPersistenceRepository) {
        this.planPersistenceRepository = planPersistenceRepository;
    }


    /**
     * Requests all plans in the subscription catalog.
     */
    @Override
    public boolean existsByCode(PlanCode code) {
        return planPersistenceRepository.existsByCode(code);
    }

    /**
     * Inserts a new plan into the repository.
     */
    @Override
    public void insert(Plan plan) {
        planPersistenceRepository.save(PlanPersistenceAssembler.toPersistenceFromDomain(plan));
    }

    /**
     * Returns all plans in the subscription catalog.
     */
    @Override
    public List<Plan> findAll() {
        return planPersistenceRepository.findAll().stream()
                .map(PlanPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }
}