package com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.repositories;

import com.dymbia.sumaqagro.platform.profiles.infrastructure.persistence.jpa.entities.AgronomistAssignmentPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgronomistAssignmentPersistenceRepository
        extends JpaRepository<AgronomistAssignmentPersistenceEntity, Long> {

    Optional<AgronomistAssignmentPersistenceEntity> findByPlotIdAndActiveTrue(Long plotId);

    List<AgronomistAssignmentPersistenceEntity> findAllByCooperativeId(Long cooperativeId);

    List<AgronomistAssignmentPersistenceEntity> findAllByProducerUserId(Long producerUserId);

    List<AgronomistAssignmentPersistenceEntity> findAllByAgronomistUserId(Long agronomistUserId);

    List<AgronomistAssignmentPersistenceEntity> findAllByPlotId(Long plotId);

    boolean existsByPlotIdAndActiveTrue(Long plotId);
}
