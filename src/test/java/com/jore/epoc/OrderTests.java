package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.YearMonth;

import org.junit.jupiter.api.Test;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.CreditEventDirection;
import com.jore.epoc.bo.accounting.Accounting;
import com.jore.epoc.bo.accounting.SimpleAccounting;
import com.jore.epoc.bo.orders.AdjustCreditLineOrder;
import com.jore.epoc.bo.orders.BuildFactoryOrder;

class OrderTests {
    @Test
    public void testBuildFactoryOrderInsufficientFunds() {
        Company company = new Company();
        BuildFactoryOrder order = new BuildFactoryOrder();
        order.setExecutionMonth(YearMonth.of(2023, 1));
        order.setConstructionCosts(Money.of("CHF", 1000000));
        order.setProductionLines(10);
        order.setMonthlyCapacityPerProductionLine(100);
        order.setTimeToBuild(0);
        order.setUnitLabourCost(Money.of("CHF", 1));
        order.setUnitProductionCost(Money.of("CHF", 1));
        order.setConstructionCostsPerLine(Money.of("CHF", 1000));
        company.addSimulationOrder(order);
        order.apply();
        assertEquals(1, company.getMessages().size());
        assertEquals(YearMonth.of(2023, 2), order.getExecutionMonth());
        assertFalse(order.isExecuted());
    }

    @Test
    public void testBuildFactoryOrderSufficientFunds() {
        Company company = new Company();
        SimpleAccounting accountingStub = (SimpleAccounting) company.getAccounting();
        accountingStub.setBalanceForAccount(Accounting.BANK, Money.of("CHF", 1100000));
        BuildFactoryOrder order = new BuildFactoryOrder();
        order.setExecutionMonth(YearMonth.of(2023, 1));
        order.setConstructionCosts(Money.of("CHF", 1000000));
        order.setProductionLines(10);
        order.setMonthlyCapacityPerProductionLine(100);
        order.setTimeToBuild(0);
        order.setUnitLabourCost(Money.of("CHF", 1));
        order.setUnitProductionCost(Money.of("CHF", 1));
        order.setConstructionCostsPerLine(Money.of("CHF", 1000));
        company.addSimulationOrder(order);
        order.apply();
        assertEquals(0, company.getMessages().size());
        assertTrue(order.isExecuted());
        assertEquals(Money.of("CHF", 90000), accountingStub.getBalanceForAccount(Accounting.BANK));
        assertEquals(Money.of("CHF", 1010000), accountingStub.getBalanceForAccount(Accounting.IMMOBILIEN));
        assertTrue(order.isExecuted());
    }

    @Test
    public void testDecreaseCreditAmount() {
        Company company = new Company();
        SimpleAccounting accountingStub = (SimpleAccounting) company.getAccounting();
        AdjustCreditLineOrder order = new AdjustCreditLineOrder();
        order.setExecutionMonth(YearMonth.of(2023, 1));
        order.setDirection(CreditEventDirection.INCREASE);
        order.setAdjustAmount(Money.of("CHF", 100001));
        company.addSimulationOrder(order);
        order.apply();
        assertEquals(Money.of("CHF", 100001), accountingStub.getBalanceForAccount(Accounting.BANK));
        assertEquals(Money.of("CHF", 100001).negate(), accountingStub.getBalanceForAccount(Accounting.LONG_TERM_DEBT));
        assertEquals(1, company.getMessages().size());
    }

    @Test
    public void testIncreaseCreditAmount() {
        Company company = new Company();
        SimpleAccounting accountingStub = (SimpleAccounting) company.getAccounting();
        AdjustCreditLineOrder order = new AdjustCreditLineOrder();
        order.setExecutionMonth(YearMonth.of(2023, 1));
        order.setDirection(CreditEventDirection.DECREASE);
        order.setAdjustAmount(Money.of("CHF", 100001));
        company.addSimulationOrder(order);
        order.apply();
        assertEquals(Money.of("CHF", 100001).negate(), accountingStub.getBalanceForAccount(Accounting.BANK));
        assertEquals(Money.of("CHF", 100001), accountingStub.getBalanceForAccount(Accounting.LONG_TERM_DEBT));
        assertEquals(1, company.getMessages().size());
    }
}
