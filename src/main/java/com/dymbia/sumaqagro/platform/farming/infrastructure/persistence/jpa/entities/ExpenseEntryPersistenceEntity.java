package com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.entities;

import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.ExpenseCategory;
import com.dymbia.sumaqagro.platform.farming.infrastructure.persistence.jpa.embeddables.MoneyPersistenceEmbeddable;
import com.dymbia.sumaqagro.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * JPA mapping of one expense of a cost ledger.
 *
 * @since 1.1
 */
@Entity
@Table(name = "expense_entries")
@Getter
@Setter
@NoArgsConstructor
public class ExpenseEntryPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campaign_ledger_id", nullable = false)
    private CampaignLedgerPersistenceEntity campaignLedger;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private ExpenseCategory category;

    @Column(name = "description", nullable = false, length = 120)
    private String description;

    @Column(name = "quantity", nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "unit_price", nullable = false,
                    precision = 12, scale = 2)),
            @AttributeOverride(name = "currency", column = @Column(name = "unit_price_currency", nullable = false,
                    length = 3))
    })
    private MoneyPersistenceEmbeddable unitPrice;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Column(name = "notes")
    private String notes;

    @Column(name = "client_sync_id", length = 64)
    private String clientSyncId;
}
