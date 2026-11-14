package com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.assemblers;

import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.CropCampaign;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.FieldPlotId;
import com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.entities.CropCampaignPersistenceEntity;

/**
 * Converts a crop campaign between the domain aggregate and its JPA entity.
 */
public final class CropCampaignPersistenceAssembler {

    private CropCampaignPersistenceAssembler() {
    }

    /**
     * Builds the domain aggregate from a stored entity.
     *
     * @param entity the entity read from the database
     * @return the crop campaign with the stored state
     */
    public static CropCampaign toDomainFromPersistence(CropCampaignPersistenceEntity entity) {
        return CropCampaign.reconstitute(
                entity.getId(),
                new FieldPlotId(entity.getPlotId()),
                entity.getSeason(),
                entity.getCropType(),
                entity.getSeedVariety(),
                entity.isSeedVarietyCustom(),
                entity.getSowingDate(),
                entity.getStatus());
    }

    /**
     * Builds the JPA entity from a domain aggregate.
     * If the campaign has no id the entity is inserted, otherwise it is updated.
     *
     * @param cropCampaign the domain aggregate
     * @return the entity ready to save
     */
    public static CropCampaignPersistenceEntity toPersistenceFromDomain(CropCampaign cropCampaign) {
        var entity = new CropCampaignPersistenceEntity();
        entity.setId(cropCampaign.getId());
        entity.setPlotId(cropCampaign.getFieldPlotIdValue());
        entity.setSeason(cropCampaign.getSeason());
        entity.setCropType(cropCampaign.getCropType());
        entity.setSeedVariety(cropCampaign.getSeedVariety());
        entity.setSeedVarietyCustom(cropCampaign.isSeedVarietyCustom());
        entity.setSowingDate(cropCampaign.getSowingDate());
        entity.setStatus(cropCampaign.getStatus());
        return entity;
    }
}
