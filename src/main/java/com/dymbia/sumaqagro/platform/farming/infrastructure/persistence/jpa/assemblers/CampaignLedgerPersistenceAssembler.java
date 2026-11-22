package com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.assemblers;

import com.dymbia.sumaqagro.platform.farming.domain.model.aggregates.CampaignLedger;
import com.dymbia.sumaqagro.platform.farming.domain.model.entities.ExpenseEntry;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CropCampaignId;
import com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.embeddables.MoneyPersistenceEmbeddable;
import com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.entities.CampaignLedgerPersistenceEntity;
import com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.entities.ExpenseEntryPersistenceEntity;

/**
 * Translates the cost ledger between the domain model and its JPA mapping.
 *
 * @since 1.1
 */
public final class CampaignLedgerPersistenceAssembler {

    private CampaignLedgerPersistenceAssembler() {
    }

    /**
     * Rebuilds the domain ledger, with its expenses, from its JPA mapping.
     *
     * @param entity the JPA mapping
     * @return the domain ledger
     */
    public static CampaignLedger toDomainFromPersistence(CampaignLedgerPersistenceEntity entity) {
        var entries = entity.getEntries().stream()
                .map(CampaignLedgerPersistenceAssembler::toDomainFromPersistence)
                .toList();
        return CampaignLedger.reconstitute(
                entity.getId(),
                new CropCampaignId(entity.getCropCampaignId()),
                entity.getYieldUnit(),
                entity.getExpectedYield(),
                entity.getActualYield(),
                entity.isFrozen(),
                entity.getCurrency(),
                entries);
    }

    /**
     * Builds the JPA mapping, with its expenses, from the domain ledger.
     * New expenses have no id, so they are inserted; the others are updated.
     *
     * @param ledger the domain ledger
     * @return the JPA mapping
     */
    public static CampaignLedgerPersistenceEntity toPersistenceFromDomain(CampaignLedger ledger) {
        var entity = new CampaignLedgerPersistenceEntity();
        entity.setId(ledger.getId());
        entity.setCropCampaignId(ledger.getCropCampaignId().cropCampaignId());
        entity.setYieldUnit(ledger.getYieldUnit());
        entity.setExpectedYield(ledger.getExpectedYield());
        entity.setActualYield(ledger.getActualYield());
        entity.setFrozen(ledger.isFrozen());
        entity.setCurrency(ledger.getCurrency());
        ledger.getEntries().forEach(entry -> entity.addEntry(toPersistenceFromDomain(entry)));
        return entity;
    }

    private static ExpenseEntry toDomainFromPersistence(ExpenseEntryPersistenceEntity entity) {
        return ExpenseEntry.reconstitute(
                entity.getId(),
                entity.getCategory(),
                entity.getDescription(),
                entity.getQuantity(),
                entity.getUnitPrice().toDomain(),
                entity.getExpenseDate(),
                entity.getNotes(),
                entity.getClientSyncId());
    }

    private static ExpenseEntryPersistenceEntity toPersistenceFromDomain(ExpenseEntry entry) {
        var entity = new ExpenseEntryPersistenceEntity();
        entity.setId(entry.getId());
        entity.setCategory(entry.getCategory());
        entity.setDescription(entry.getDescription());
        entity.setQuantity(entry.getQuantity());
        entity.setUnitPrice(new MoneyPersistenceEmbeddable(entry.getUnitPrice()));
        entity.setExpenseDate(entry.getExpenseDate());
        entity.setNotes(entry.getNotes());
        entity.setClientSyncId(entry.getClientSyncId());
        return entity;
    }
}
