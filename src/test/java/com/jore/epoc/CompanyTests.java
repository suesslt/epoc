package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.orders.AbstractSimulationOrder;
import com.jore.epoc.bo.orders.AdjustCreditLineOrder;
import com.jore.epoc.bo.orders.BuildFactoryOrder;
import com.jore.epoc.bo.orders.BuildStorageOrder;
import com.jore.epoc.bo.orders.BuyRawMaterialOrder;
import com.jore.epoc.bo.orders.EnterMarketOrder;

class CompanyTests {
    @Test
    public void testSortingOfOrders() {
        Company company = new Company();
        company.addSimulationOrder(createEnterMarketOrder());
        company.addSimulationOrder(createBuyRawMaterialOrder());
        company.addSimulationOrder(createBuildStorageOrder());
        company.addSimulationOrder(createAdjustCreditLineOrder());
        company.addSimulationOrder(createBuildFactoryOrder());
        List<AbstractSimulationOrder> simulationOrders = company.getOrdersForExecutionIn(YearMonth.of(2020, 1));
        assertEquals(1, simulationOrders.get(0).getSortOrder());
        assertEquals(2, simulationOrders.get(1).getSortOrder());
        assertEquals(3, simulationOrders.get(2).getSortOrder());
        assertEquals(4, simulationOrders.get(3).getSortOrder());
        assertEquals(5, simulationOrders.get(4).getSortOrder());
    }

    private AbstractSimulationOrder createAdjustCreditLineOrder() {
        AbstractSimulationOrder result = new AdjustCreditLineOrder();
        result.setExecutionMonth(YearMonth.of(2020, 1));
        return result;
    }

    private BuildFactoryOrder createBuildFactoryOrder() {
        BuildFactoryOrder result = new BuildFactoryOrder();
        result.setExecutionMonth(YearMonth.of(2020, 1));
        return result;
    }

    private BuildStorageOrder createBuildStorageOrder() {
        BuildStorageOrder result = new BuildStorageOrder();
        result.setExecutionMonth(YearMonth.of(2020, 1));
        return result;
    }

    private BuyRawMaterialOrder createBuyRawMaterialOrder() {
        BuyRawMaterialOrder result = new BuyRawMaterialOrder();
        result.setExecutionMonth(YearMonth.of(2020, 1));
        return result;
    }

    private EnterMarketOrder createEnterMarketOrder() {
        EnterMarketOrder result = new EnterMarketOrder();
        result.setExecutionMonth(YearMonth.of(2020, 1));
        return result;
    }
}
