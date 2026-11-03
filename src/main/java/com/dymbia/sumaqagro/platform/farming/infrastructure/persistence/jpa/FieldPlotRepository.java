package com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa;

import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.FieldPlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldPlotRepository extends JpaRepository<FieldPlot, Long> {
    List<FieldPlot> findAllByOwnerUserId(Long ownerUserId);
    long countByOwnerUserId(Long ownerUserId);
    boolean existsByOwnerUserIdAndName(Long ownerUserId, String name);
}
