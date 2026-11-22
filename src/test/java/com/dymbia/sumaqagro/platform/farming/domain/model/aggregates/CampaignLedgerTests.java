package com.dymbia.sumaqagro.platform.farming.domain.model.aggregates;

import com.dymbia.sumaqagro.platform.farming.domain.model.commands.OpenCampaignLedgerCommand;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.ExpenseCategory;
import com.dymbia.sumaqagro.platform.farming.domain.model.valueobjects.YieldUnit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CampaignLedgerTests {
    private static final LocalDate DATE = LocalDate.of(2026, 10, 15);

    private static CampaignLedger openLedger() {
        return new CampaignLedger(new OpenCampaignLedgerCommand(1L, YieldUnit.SACK));
    }

    @Test
    void newLedgerIsEmptyAndOpen() {
        var ledger = openLedger();

        assertEquals(new BigDecimal("0.00"), ledger.totalInvestment().amount());
        assertFalse(ledger.isFrozen());
    }

    @Test
    void laborExpenseIsWorkersByDaysByDailyWage() {
        var ledger = openLedger();

        ledger.recordDailyLaborExpense("Siembra", 4, 2, new BigDecimal("50"), DATE, null);

        assertEquals(new BigDecimal("400.00"), ledger.totalByCategory(ExpenseCategory.LABOR).amount());
    }

    @Test
    void breakevenPriceIsTheInvestmentDividedByTheYield() {
        var ledger = openLedger();
        ledger.recordInputExpense("Urea", new BigDecimal("2"), new BigDecimal("170"), DATE, null);
        ledger.recordFieldFreightExpense("Chacra - acopio", new BigDecimal("3660"), DATE, null);
        ledger.estimateYield(new BigDecimal("100"), null);

        assertEquals(new BigDecimal("4000.00"), ledger.totalInvestment().amount());
        assertEquals(new BigDecimal("40.00"), ledger.breakevenPrice().amount());
    }

    @Test
    void anExpenseSentTwiceFromTheDeviceIsSavedOnce() {
        var ledger = openLedger();

        var first = ledger.recordInputExpense("Urea", BigDecimal.ONE, new BigDecimal("170"), DATE, "sync-1");
        var second = ledger.recordInputExpense("Urea", BigDecimal.ONE, new BigDecimal("170"), DATE, "sync-1");

        assertSame(first, second);
        assertEquals(1, ledger.getEntries().size());
    }

    @Test
    void frozenLedgerRejectsExpenses() {
        var ledger = openLedger();
        ledger.freeze();

        var exception = assertThrows(IllegalStateException.class, () -> ledger.recordInputExpense("Urea",
                BigDecimal.ONE, new BigDecimal("170"), DATE, null));

        assertEquals("campaign.ledger.error.frozen", exception.getMessage());
    }
}