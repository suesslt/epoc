package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.YearMonth;

import org.junit.jupiter.api.Test;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.CreditEventDirection;
import com.jore.epoc.bo.Market;
import com.jore.epoc.bo.MarketSimulation;
import com.jore.epoc.bo.Storage;
import com.jore.epoc.bo.accounting.FinancialAccounting;
import com.jore.epoc.bo.orders.AdjustCreditLineOrder;
import com.jore.epoc.bo.orders.BuildFactoryOrder;
import com.jore.epoc.bo.orders.BuildStorageOrder;
import com.jore.epoc.bo.orders.BuyRawMaterialOrder;
import com.jore.epoc.bo.orders.EnterMarketOrder;

class OrderTests {
    @Test
    public void testBuildFactoryOrderInsufficientFunds() {
        Company company = new Company();
        FinancialAccounting accounting = new FinancialAccounting();
        company.setAccounting(accounting);
        BuildFactoryOrder order = new BuildFactoryOrder();
        order.setExecutionMonth(YearMonth.of(2023, 1));
        order.setConstructionCosts(Money.of("CHF", 1000000));
        order.setProductionLines(10);
        order.setMonthlyCapacityPerProductionLine(100);
        order.setTimeToBuild(0);
        order.setUnitLaborCost(Money.of("CHF", 1));
        order.setUnitProductionCost(Money.of("CHF", 1));
        order.setConstructionCostsPerLine(Money.of("CHF", 1000));
        company.addSimulationOrder(order);
        order.execute();
        assertEquals(1, company.getMessages().size());
        assertEquals(YearMonth.of(2023, 1), order.getExecutionMonth());
        assertFalse(order.isExecuted());
    }

    @Test
    public void testBuildFactoryOrderSufficientFunds() {
        Company company = new Company();
        FinancialAccounting accounting = new FinancialAccounting();
        company.setAccounting(accounting);
        accounting.setBalanceForAccount(FinancialAccounting.BANK, Money.of("CHF", 1100000));
        BuildFactoryOrder order = new BuildFactoryOrder();
        order.setExecutionMonth(YearMonth.of(2023, 1));
        order.setConstructionCosts(Money.of("CHF", 1000000));
        order.setProductionLines(10);
        order.setMonthlyCapacityPerProductionLine(100);
        order.setTimeToBuild(0);
        order.setUnitLaborCost(Money.of("CHF", 1));
        order.setUnitProductionCost(Money.of("CHF", 1));
        order.setConstructionCostsPerLine(Money.of("CHF", 1000));
        company.addSimulationOrder(order);
        order.execute();
        assertEquals(1, company.getMessages().size());
        assertTrue(order.isExecuted());
        assertEquals(Money.of("CHF", 90000), accounting.getBalanceForAccount(FinancialAccounting.BANK));
        assertEquals(Money.of("CHF", 1010000), accounting.getBalanceForAccount(FinancialAccounting.REAL_ESTATE));
    }

    @Test
    public void testBuildStorageOrderInsufficientFunds() {
        Company company = new Company();
        FinancialAccounting accounting = new FinancialAccounting();
        company.setAccounting(accounting);
        BuildStorageOrder order = new BuildStorageOrder();
        order.setExecutionMonth(YearMonth.of(2023, 1));
        order.setConstructionCosts(Money.of("CHF", 1000000));
        order.setCapacity(1000);
        order.setConstructionCostsPerUnit(Money.of("CHF", 1));
        order.setTimeToBuild(0);
        company.addSimulationOrder(order);
        order.execute();
        assertEquals(1, company.getMessages().size());
        assertEquals(YearMonth.of(2023, 1), order.getExecutionMonth());
        assertFalse(order.isExecuted());
    }

    @Test
    public void testBuildStorageOrderSufficientFunds() {
        Company company = new Company();
        FinancialAccounting accounting = new FinancialAccounting();
        company.setAccounting(accounting);
        accounting.setBalanceForAccount(FinancialAccounting.BANK, Money.of("CHF", 1100000));
        BuildStorageOrder order = new BuildStorageOrder();
        order.setExecutionMonth(YearMonth.of(2023, 1));
        order.setConstructionCosts(Money.of("CHF", 1000000));
        order.setCapacity(1000);
        order.setConstructionCostsPerUnit(Money.of("CHF", 1));
        order.setTimeToBuild(0);
        company.addSimulationOrder(order);
        order.execute();
        assertEquals(1, company.getMessages().size());
        assertEquals(YearMonth.of(2023, 1), order.getExecutionMonth());
        assertTrue(order.isExecuted());
        assertEquals(Money.of("CHF", 99000), accounting.getBalanceForAccount(FinancialAccounting.BANK));
        assertEquals(Money.of("CHF", 1001000), accounting.getBalanceForAccount(FinancialAccounting.REAL_ESTATE));
    }

    @Test
    public void testBuyRawMaterialOrderInsufficientFunds() {
        Company company = new Company();
        FinancialAccounting accounting = new FinancialAccounting();
        company.setAccounting(accounting);
        Storage storage = new Storage();
        storage.setCapacity(1001);
        storage.setStorageStartMonth(YearMonth.of(2023, 1));
        company.addStorage(storage);
        accounting.setBalanceForAccount(FinancialAccounting.BANK, Money.of("CHF", 29999));
        BuyRawMaterialOrder order = new BuyRawMaterialOrder();
        order.setExecutionMonth(YearMonth.of(2023, 1));
        order.setAmount(1000);
        order.setUnitPrice(Money.of("CHF", 30));
        company.addSimulationOrder(order);
        order.execute();
        assertEquals(1, company.getMessages().size());
        assertEquals(YearMonth.of(2023, 1), order.getExecutionMonth());
        assertFalse(order.isExecuted());
        assertEquals(Money.of("CHF", 29999), accounting.getBalanceForAccount(FinancialAccounting.BANK));
        assertEquals(Money.of("CHF", 0), accounting.getBalanceForAccount(FinancialAccounting.ROHWAREN));
    }

    @Test
    public void testBuyRawMaterialOrderInsufficientStorageCapacity() {
        Company company = new Company();
        FinancialAccounting accounting = new FinancialAccounting();
        company.setAccounting(accounting);
        Storage storage = new Storage();
        storage.setCapacity(100);
        storage.setStorageStartMonth(YearMonth.of(2023, 1));
        company.addStorage(storage);
        accounting.setBalanceForAccount(FinancialAccounting.BANK, Money.of("CHF", 30001));
        BuyRawMaterialOrder order = new BuyRawMaterialOrder();
        order.setExecutionMonth(YearMonth.of(2023, 1));
        order.setAmount(1000);
        order.setUnitPrice(Money.of("CHF", 30));
        company.addSimulationOrder(order);
        order.execute();
        assertEquals(1, company.getMessages().size());
        assertEquals(YearMonth.of(2023, 1), order.getExecutionMonth());
        assertFalse(order.isExecuted());
        assertEquals(Money.of("CHF", 30001), accounting.getBalanceForAccount(FinancialAccounting.BANK));
        assertEquals(Money.of("CHF", 0), accounting.getBalanceForAccount(FinancialAccounting.ROHWAREN));
    }

    @Test
    public void testBuyRawMaterialOrderSufficientFunds() {
        Company company = new Company();
        FinancialAccounting accounting = new FinancialAccounting();
        company.setAccounting(accounting);
        Storage storage = new Storage();
        storage.setCapacity(1001);
        storage.setStorageStartMonth(YearMonth.of(2023, 1));
        company.addStorage(storage);
        accounting.setBalanceForAccount(FinancialAccounting.BANK, Money.of("CHF", 30001));
        BuyRawMaterialOrder order = new BuyRawMaterialOrder();
        order.setExecutionMonth(YearMonth.of(2023, 1));
        order.setAmount(1000);
        order.setUnitPrice(Money.of("CHF", 30));
        company.addSimulationOrder(order);
        order.execute();
        assertEquals(1, company.getMessages().size());
        assertEquals(YearMonth.of(2023, 1), order.getExecutionMonth());
        assertTrue(order.isExecuted());
        assertEquals(Money.of("CHF", 1), accounting.getBalanceForAccount(FinancialAccounting.BANK));
        assertEquals(Money.of("CHF", 30000), accounting.getBalanceForAccount(FinancialAccounting.ROHWAREN));
    }

    @Test
    public void testDecreaseCreditAmountSuccessfully() {
        Company company = new Company();
        FinancialAccounting accounting = new FinancialAccounting();
        accounting.setBalanceForAccount(FinancialAccounting.BANK, Money.of("CHF", 100002));
        company.setAccounting(accounting);
        AdjustCreditLineOrder order = new AdjustCreditLineOrder();
        order.setExecutionMonth(YearMonth.of(2023, 1));
        order.setDirection(CreditEventDirection.DECREASE);
        order.setAmount(Money.of("CHF", 100001));
        company.addSimulationOrder(order);
        order.execute();
        assertEquals(1, company.getMessages().size());
        assertTrue(order.isExecuted());
        assertEquals(Money.of("CHF", 1), accounting.getBalanceForAccount(FinancialAccounting.BANK));
    }

    @Test
    public void testDecreaseCreditAmountUnsuccessfully() {
        Company company = new Company();
        FinancialAccounting accounting = new FinancialAccounting();
        company.setAccounting(accounting);
        AdjustCreditLineOrder order = new AdjustCreditLineOrder();
        order.setExecutionMonth(YearMonth.of(2023, 1));
        order.setDirection(CreditEventDirection.DECREASE);
        order.setAmount(Money.of("CHF", 100001));
        company.addSimulationOrder(order);
        order.execute();
        assertEquals(1, company.getMessages().size());
        assertFalse(order.isExecuted());
        assertEquals(Money.of("CHF", 0), accounting.getBalanceForAccount(FinancialAccounting.BANK));
    }

    @Test
    public void testEnterMarketOrderSuccess() {
        Market market = new Market();
        market.setName("Europe");
        MarketSimulation marketSimulation = new MarketSimulation();
        marketSimulation.setMarket(market);
        Company company = new Company();
        FinancialAccounting accounting = new FinancialAccounting();
        company.setAccounting(accounting);
        accounting.setBalanceForAccount(FinancialAccounting.BANK, Money.of("CHF", 100000));
        EnterMarketOrder order = new EnterMarketOrder();
        order.setExecutionMonth(YearMonth.of(2023, 1));
        order.setFixedCosts(Money.of("CHF", 100000));
        order.setIntentedProductSale(1000);
        order.setOfferedPrice(Money.of("CHF", 80));
        order.setMarketSimulation(marketSimulation);
        company.addSimulationOrder(order);
        order.execute();
        assertTrue(order.isExecuted());
        assertEquals(1, company.getMessages().size());
        assertEquals(Money.of("CHF", 0), accounting.getBalanceForAccount(FinancialAccounting.BANK));
        assertEquals(Money.of("CHF", 100000).negate(), accounting.getBalanceForAccount(FinancialAccounting.SERVICES));
    }

    @Test
    public void testIncreaseCreditAmount() {
        Company company = new Company();
        FinancialAccounting accounting = new FinancialAccounting();
        company.setAccounting(accounting);
        AdjustCreditLineOrder order = new AdjustCreditLineOrder();
        order.setExecutionMonth(YearMonth.of(2023, 1));
        order.setDirection(CreditEventDirection.INCREASE);
        order.setAmount(Money.of("CHF", 100001));
        company.addSimulationOrder(order);
        order.execute();
        assertEquals(1, company.getMessages().size());
        assertTrue(order.isExecuted());
        assertEquals(Money.of("CHF", 100001), accounting.getBalanceForAccount(FinancialAccounting.BANK));
        assertEquals(Money.of("CHF", 100001).negate(), accounting.getBalanceForAccount(FinancialAccounting.LONG_TERM_DEBT));
    }
}
