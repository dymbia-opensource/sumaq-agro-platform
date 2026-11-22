package com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.entities;

import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.YieldUnit;
import com.dymbia.sumaqagro.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA mapping of the cost ledger aggregate. One ledger per crop campaign.
 *
 * @since 1.1
 */
@Entity
@Table(name = "campaign_ledgers")
@Getter
@Setter
@NoArgsConstructor
public class CampaignLedgerPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "crop_campaign_id", nullable = false, unique = true)
    private Long cropCampaignId;

    @Enumerated(EnumType.STRING)
    @Column(name = "yield_unit", nullable = false, length = 20)
    private YieldUnit yieldUnit;

    @Column(name = "expected_yield", precision = 12, scale = 2)
    private BigDecimal expectedYield;

    @Column(name = "actual_yield", precision = 12, scale = 2)
    private BigDecimal actualYield;

    @Column(name = "frozen", nullable = false)
    private boolean frozen;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @OneToMany(mappedBy = "campaignLedger", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("expenseDate ASC, id ASC")
    private List<ExpenseEntryPersistenceEntity> entries = new ArrayList<>();

    /**
     * Adds an expense and keeps both sides of the relation in sync, so the foreign key is filled.
     *
     * @param entry the expense to add
     */
    public void addEntry(ExpenseEntryPersistenceEntity entry) {
        entry.setCampaignLedger(this);
        entries.add(entry);
    }
}
