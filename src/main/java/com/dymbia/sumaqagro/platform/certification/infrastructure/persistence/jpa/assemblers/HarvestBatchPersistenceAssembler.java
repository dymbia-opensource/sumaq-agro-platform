package com.dymbia.sumaqagro.platform.certification.infrastructure.persistence.jpa.assemblers;

import com.dymbia.sumaqagro.platform.certification.domain.model.aggregates.HarvestBatch;
import com.dymbia.sumaqagro.platform.certification.infrastructure.persistence.jpa.entities.HarvestBatchPersistenceEntity;

/**
 * Converts a harvest batch between the domain aggregate and its JPA entity.
 */
public final class HarvestBatchPersistenceAssembler {

    private HarvestBatchPersistenceAssembler() {
    }

    /**
     * Builds the domain aggregate from a stored entity.
     *
     * @param entity the entity read from the database
     * @return the harvest batch with the stored state
     */
    public static HarvestBatch toDomainFromPersistence(HarvestBatchPersistenceEntity entity) {
        return HarvestBatch.reconstitute(
                entity.getId(),
                entity.getBatchCode(),
                entity.getCooperativeId(),
                entity.getMemberId(),
                entity.getPlotId(),
                entity.getCropType(),
                entity.getHarvestedOn(),
                entity.getRegisteredAt(),
                entity.getStatus(),
                entity.getNetWeightKg(),
                entity.getSampleIdentifier(),
                entity.getSampleWeightKg(),
                entity.getFirstCaliberPercentage(),
                entity.getSecondCaliberPercentage(),
                entity.getThirdCaliberPercentage(),
                entity.getPestDamagePercentage(),
                entity.getCoffeeCuppingScore(),
                entity.isCoffeeFermentDefect(),
                entity.getQualityScore(),
                entity.getQualityCategory());
    }

    /**
     * Builds the JPA entity from a domain aggregate.
     * If the batch has no id the entity is inserted, otherwise it is updated.
     *
     * @param harvestBatch the domain aggregate
     * @return the entity ready to save
     */
    public static HarvestBatchPersistenceEntity toPersistenceFromDomain(HarvestBatch harvestBatch) {
        var entity = new HarvestBatchPersistenceEntity();
        entity.setId(harvestBatch.getId());
        entity.setBatchCode(harvestBatch.getBatchCode());
        entity.setCooperativeId(harvestBatch.getCooperativeId());
        entity.setMemberId(harvestBatch.getMemberId());
        entity.setPlotId(harvestBatch.getPlotId());
        entity.setCropType(harvestBatch.getCropType());
        entity.setHarvestedOn(harvestBatch.getHarvestedOn());
        entity.setRegisteredAt(harvestBatch.getRegisteredAt());
        entity.setStatus(harvestBatch.getStatus());
        entity.setNetWeightKg(harvestBatch.getNetWeightKg());
        entity.setSampleIdentifier(harvestBatch.getSampleIdentifier());
        entity.setSampleWeightKg(harvestBatch.getSampleWeightKg());
        entity.setFirstCaliberPercentage(harvestBatch.getFirstCaliberPercentage());
        entity.setSecondCaliberPercentage(harvestBatch.getSecondCaliberPercentage());
        entity.setThirdCaliberPercentage(harvestBatch.getThirdCaliberPercentage());
        entity.setPestDamagePercentage(harvestBatch.getPestDamagePercentage());
        entity.setCoffeeCuppingScore(harvestBatch.getCoffeeCuppingScore());
        entity.setCoffeeFermentDefect(harvestBatch.isCoffeeFermentDefect());
        entity.setQualityScore(harvestBatch.getQualityScore());
        entity.setQualityCategory(harvestBatch.getQualityCategory());
        return entity;
    }
}
