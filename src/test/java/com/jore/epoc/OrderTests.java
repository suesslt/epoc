package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.YearMonth;

import org.junit.jupiter.api.Test;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.CreditEventDirection;
import com.jore.epoc.bo.accounting.MilchbuechliAccounting;
import com.jore.epoc.bo.orders.AdjustCreditLineOrder;

class OrderTests {
    @Test
    public void testIncreaseCreditAmount() {
        Company company = new Company();
        MilchbuechliAccounting accountingStub = new MilchbuechliAccounting();
        company.setAccounting(accountingStub);
        AdjustCreditLineOrder order = new AdjustCreditLineOrder();
        order.setExecutionMonth(YearMonth.of(2023, 1));
        order.setDirection(CreditEventDirection.INCREASE);
        order.setAdjustAmount(Money.of("CHF", 100001));
        order.apply(company);
        assertEquals(Money.of("CHF", 100001), accountingStub.getBalanceForAccount("1020"));
        assertEquals(Money.of("CHF", 100001).negate(), accountingStub.getBalanceForAccount("2450"));
        assertEquals(1, company.getMessages().size());
    }
}
