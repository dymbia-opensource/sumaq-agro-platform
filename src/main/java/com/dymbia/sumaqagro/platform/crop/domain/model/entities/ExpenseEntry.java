package com.dymbia.sumaqagro.platform.crop.domain.model.entities;

import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.ExpenseCategory;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.Money;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Expense recorded in the cost ledger of a campaign.
 * <p>
 * It is an entity of the {@code CampaignLedger} aggregate: it has an id but it only
 * exists inside its ledger, which is the one that creates and saves it.
 * </p>
 *
 * @since 1.1
 */
@Getter
public class ExpenseEntry {
    private static final int MAX_DESCRIPTION_LENGTH = 120;

    private Long id;
    private final ExpenseCategory category;
    private final String description;
    private final BigDecimal quantity;
    private final Money unitPrice;
    private final LocalDate expenseDate;
    private final String notes;
    private final String clientSyncId;

    public ExpenseEntry(ExpenseCategory category, String description, BigDecimal quantity, Money unitPrice,
                        LocalDate expenseDate, String notes, String clientSyncId) {
        if (category == null) {
            throw new IllegalArgumentException("expense.entry.error.category.required");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("expense.entry.error.description.notBlank");
        }
        if (description.trim().length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException("expense.entry.error.description.size");
        }
        if (quantity == null || quantity.signum() <= 0) {
            throw new IllegalArgumentException("expense.entry.error.quantity.invalid");
        }
        if (unitPrice == null || unitPrice.amount().signum() <= 0) {
            throw new IllegalArgumentException("expense.entry.error.unitPrice.invalid");
        }
        if (expenseDate == null || expenseDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("expense.entry.error.expenseDate.invalid");
        }
        this.category = category;
        this.description = description.trim();
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.expenseDate = expenseDate;
        if (notes == null || notes.isBlank()) {
            this.notes = null;
        } else {
            this.notes = notes.trim();
        }
        if (clientSyncId == null || clientSyncId.isBlank()) {
            this.clientSyncId = null;
        } else {
            this.clientSyncId = clientSyncId.trim();
        }
    }

    /**
     * Rebuilds an expense that already exists in the database.
     */
    public static ExpenseEntry reconstitute(Long id, ExpenseCategory category, String description,
                                            BigDecimal quantity, Money unitPrice, LocalDate expenseDate, String notes
            , String clientSyncId) {
        var entry = new ExpenseEntry(category, description, quantity, unitPrice, expenseDate, notes, clientSyncId);
        entry.id = id;
        return entry;
    }

    /**
     * Total of the expense: quantity by unit price.
     */
    public Money total() {
        return unitPrice.multiply(quantity);
    }

    /**
     * Tells whether the expense was sent from the device with this sync id.
     * It avoids saving twice an expense that the offline mode sends again.
     */
    public boolean hasClientSyncId(String clientSyncId) {
        return this.clientSyncId != null && this.clientSyncId.equals(clientSyncId);
    }
}
