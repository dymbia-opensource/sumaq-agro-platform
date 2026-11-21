package com.dymbia.sumaqagro.platform.farming.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.OpenCampaignLedgerCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.entities.ExpenseEntry;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.CropCampaignId;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.ExpenseCategory;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.Money;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.YieldUnit;
import com.dymbia.sumaqagro.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CampaignLedger extends AbstractDomainAggregateRoot<CampaignLedger> {
    private static final String FROZEN_MESSAGE_KEY = "campaign.ledger.error.frozen";

    private Long id;
    private CropCampaignId cropCampaignId;
    private YieldUnit yieldUnit;
    private BigDecimal expectedYield;
    private BigDecimal actualYield;
    private boolean frozen;
    private String currency;
    private final List<ExpenseEntry> entries = new ArrayList<>();

    private CampaignLedger() {
    }

    /**
     * Opens an empty ledger for a campaign.
     */
    public CampaignLedger(OpenCampaignLedgerCommand command) {
        this.cropCampaignId = new CropCampaignId(command.cropCampaignId());
        this.yieldUnit = command.yieldUnit();
        this.expectedYield = BigDecimal.ZERO;
        this.frozen = false;
        this.currency = Money.DEFAULT_CURRENCY;
    }

    /**
     * Rebuilds a ledger that already exists in the database. It raises no events.
     */
    public static CampaignLedger reconstitute(Long id, CropCampaignId cropCampaignId, YieldUnit yieldUnit,
                                              BigDecimal expectedYield, BigDecimal actualYield, boolean frozen,
                                              String currency, List<ExpenseEntry> entries) {
        var ledger = new CampaignLedger();
        ledger.id = id;
        ledger.cropCampaignId = cropCampaignId;
        ledger.yieldUnit = yieldUnit;
        ledger.expectedYield = expectedYield;
        ledger.actualYield = actualYield;
        ledger.frozen = frozen;
        ledger.currency = currency;
        if (entries != null) {
            ledger.entries.addAll(entries);
        }
        return ledger;
    }

    /**
     * Records the purchase of seeds, fertilizers or pesticides (US-37).
     */
    public ExpenseEntry recordInputExpense(String inputName, BigDecimal quantity, BigDecimal unitPrice,
                                           LocalDate expenseDate, String clientSyncId) {
        return recordExpense(ExpenseCategory.INPUTS, inputName, quantity, unitPrice, expenseDate, clientSyncId);
    }

    /**
     * Records the wages of the field workers: workers by days by daily wage (US-38).
     */
    public ExpenseEntry recordDailyLaborExpense(String activity, int workers, int days, BigDecimal dailyWage,
                                                LocalDate expenseDate, String clientSyncId) {
        if (workers <= 0) {
            throw new IllegalArgumentException("expense.entry.error.labor.workers.invalid");
        }
        if (days <= 0) {
            throw new IllegalArgumentException("expense.entry.error.labor.days.invalid");
        }
        var workDays = BigDecimal.valueOf((long) workers * days);
        return recordExpense(ExpenseCategory.LABOR, activity, workDays, dailyWage, expenseDate, clientSyncId);
    }

    /**
     * Records the transport of inputs or harvest (US-39).
     */
    public ExpenseEntry recordFieldFreightExpense(String route, BigDecimal cost, LocalDate expenseDate,
                                                  String clientSyncId) {
        return recordExpense(ExpenseCategory.FREIGHT, route, BigDecimal.ONE, cost, expenseDate, clientSyncId);
    }

    /**
     * Records the expected yield and, after the harvest, the actual yield (US-40).
     */
    public void estimateYield(BigDecimal expectedYield, BigDecimal actualYield) {
        ensureNotFrozen();
        if (expectedYield == null || expectedYield.signum() <= 0) {
            throw new IllegalArgumentException("campaign.ledger.error.expectedYield.invalid");
        }
        if (actualYield != null && actualYield.signum() < 0) {
            throw new IllegalArgumentException("campaign.ledger.error.actualYield.invalid");
        }
        this.expectedYield = expectedYield;
        this.actualYield = actualYield;
    }

    /**
     * Freezes the ledger when its campaign finishes (US-35).
     */
    public void freeze() {
        this.frozen = true;
    }

    /**
     * Gets the expenses as a read-only list: they only change through the methods of the ledger.
     */
    public List<ExpenseEntry> getEntries() {
        return List.copyOf(entries);
    }

    public Money totalInvestment() {
        return entries.stream().map(ExpenseEntry::total).reduce(Money.zero(currency), Money::add);
    }

    public Money totalByCategory(ExpenseCategory category) {
        return entries.stream().filter(entry -> entry.getCategory() == category).map(ExpenseEntry::total).reduce(Money.zero(currency), Money::add);
    }

    /**
     * The actual yield once it is known, the expected one before.
     */
    public BigDecimal referenceYield() {
        return actualYield != null && actualYield.signum() > 0 ? actualYield : expectedYield;
    }

    /**
     * Minimum price per sack or quintal that covers the investment (US-40).
     */
    public Money breakevenPrice() {
        var yield = referenceYield();
        return yield == null || yield.signum() <= 0 ? Money.zero(currency) : totalInvestment().divide(yield);
    }

    private ExpenseEntry recordExpense(ExpenseCategory category, String description, BigDecimal quantity,
                                       BigDecimal unitPrice, LocalDate expenseDate, String clientSyncId) {
        ensureNotFrozen();
        if (clientSyncId != null && !clientSyncId.isBlank()) {
            var alreadyRecorded =
                    entries.stream().filter(entry -> entry.hasClientSyncId(clientSyncId.trim())).findFirst();
            if (alreadyRecorded.isPresent()) {
                return alreadyRecorded.get();
            }
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("expense.entry.error.unitPrice.invalid");
        }
        var entry = new ExpenseEntry(category, description, quantity, new Money(unitPrice, currency), expenseDate,
                null, clientSyncId);
        entries.add(entry);
        return entry;
    }

    // Invariant of the aggregate: a frozen ledger accepts no more changes
    private void ensureNotFrozen() {
        if (frozen) {
            throw new IllegalStateException(FROZEN_MESSAGE_KEY);
        }
    }
}
