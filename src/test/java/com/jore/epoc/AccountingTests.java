package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.jore.datatypes.currency.Currency;
import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.accounting.DebitCreditAmount;
import com.jore.epoc.bo.accounting.FinancialAccounting;

class AccountingTests {
    @Test
    void test() {
        FinancialAccounting financialAccounting = new FinancialAccounting();
        financialAccounting.setBaseCurrency(Currency.getInstance("CHF"));
        financialAccounting.book("Test booking", LocalDate.of(2023, 5, 10), LocalDate.of(2023, 5, 10), new DebitCreditAmount(FinancialAccounting.PRODUCT_REVENUES, FinancialAccounting.BANK, Money.of("CHF", 1000)));
        assertEquals(Money.of("CHF", -1000), financialAccounting.getPnL(LocalDate.of(2023, 12, 31)));
    }
}
