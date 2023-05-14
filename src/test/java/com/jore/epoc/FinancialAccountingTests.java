package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.jore.datatypes.currency.Currency;
import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.accounting.DebitCreditAmount;
import com.jore.epoc.bo.accounting.FinancialAccounting;

class FinancialAccountingTests {
    @Test
    public void testAccountNotFound() {
        FinancialAccounting accounting = new FinancialAccounting();
        accounting.setBaseCurrency(Currency.getInstance("CHF"));
        assertThrows(AssertionError.class, () -> accounting.book("Wrong debit account", LocalDate.of(2020, 1, 15), LocalDate.of(2020, 1, 17), new DebitCreditAmount("noacc", FinancialAccounting.INTEREST, Money.of("CHF", 1000000))));
        assertThrows(AssertionError.class, () -> accounting.book("Wrong credit account", LocalDate.of(2020, 1, 15), LocalDate.of(2020, 1, 17), new DebitCreditAmount(FinancialAccounting.BANK, "noacc", Money.of("CHF", 1000000))));
    }

    @Test
    public void testAfterValueDate() {
        FinancialAccounting accounting = new FinancialAccounting();
        accounting.setBaseCurrency(Currency.getInstance("CHF"));
        accounting.book("Sell products", LocalDate.of(2020, 1, 15), LocalDate.of(2020, 1, 17), new DebitCreditAmount(FinancialAccounting.BANK, FinancialAccounting.PRODUCT_REVENUES, Money.of("CHF", 1000000)));
        assertEquals(Money.of("CHF", 1000000), accounting.getPnL(LocalDate.of(2020, 1, 18)));
    }

    @Test
    public void testBeforeBookingDate() {
        FinancialAccounting accounting = new FinancialAccounting();
        accounting.setBaseCurrency(Currency.getInstance("CHF"));
        accounting.book("Sell products", LocalDate.of(2020, 1, 15), LocalDate.of(2020, 1, 17), new DebitCreditAmount(FinancialAccounting.BANK, FinancialAccounting.PRODUCT_REVENUES, Money.of("CHF", 1000000)));
        assertEquals(Money.of("CHF", 0), accounting.getPnL(LocalDate.of(2020, 1, 14)));
    }

    @Test
    public void testBetweenBookingAndValueDate() {
        FinancialAccounting accounting = new FinancialAccounting();
        accounting.setBaseCurrency(Currency.getInstance("CHF"));
        accounting.book("Sell products", LocalDate.of(2020, 1, 15), LocalDate.of(2020, 1, 17), new DebitCreditAmount(FinancialAccounting.BANK, FinancialAccounting.PRODUCT_REVENUES, Money.of("CHF", 1000000)));
        assertEquals(Money.of("CHF", 0), accounting.getPnL(LocalDate.of(2020, 1, 16)));
    }

    @Test
    public void testDebitCreditAmountMissing() {
        FinancialAccounting accounting = new FinancialAccounting();
        accounting.setBaseCurrency(Currency.getInstance("CHF"));
        assertThrows(AssertionError.class, () -> accounting.book("Missing amount", LocalDate.of(2020, 1, 15), LocalDate.of(2020, 1, 17)));
    }

    @Test
    public void testMissingBookingDate() {
        FinancialAccounting accounting = new FinancialAccounting();
        accounting.setBaseCurrency(Currency.getInstance("CHF"));
        assertThrows(NullPointerException.class, () -> accounting.book("Missing booking date", null, LocalDate.of(2020, 1, 17), new DebitCreditAmount(FinancialAccounting.BANK, FinancialAccounting.INTEREST, Money.of("CHF", 1000000))));
    }

    @Test
    public void testMissingText() {
        FinancialAccounting accounting = new FinancialAccounting();
        accounting.setBaseCurrency(Currency.getInstance("CHF"));
        assertThrows(NullPointerException.class, () -> accounting.book(null, LocalDate.of(2020, 1, 15), LocalDate.of(2020, 1, 17), new DebitCreditAmount(FinancialAccounting.BANK, FinancialAccounting.INTEREST, Money.of("CHF", 1000000))));
    }

    @Test
    public void testMissingValueDate() {
        FinancialAccounting accounting = new FinancialAccounting();
        accounting.setBaseCurrency(Currency.getInstance("CHF"));
        assertThrows(NullPointerException.class, () -> accounting.book("Missing value date", LocalDate.of(2020, 1, 15), null, new DebitCreditAmount(FinancialAccounting.BANK, FinancialAccounting.INTEREST, Money.of("CHF", 1000000))));
    }

    @Test
    public void testOnValueDate() {
        FinancialAccounting accounting = new FinancialAccounting();
        accounting.setBaseCurrency(Currency.getInstance("CHF"));
        accounting.book("Sell products", LocalDate.of(2020, 1, 15), LocalDate.of(2020, 1, 17), new DebitCreditAmount(FinancialAccounting.BANK, FinancialAccounting.PRODUCT_REVENUES, Money.of("CHF", 1000000)));
        assertEquals(Money.of("CHF", 1000000), accounting.getPnL(LocalDate.of(2020, 1, 17)));
    }

    @Test
    public void testOwnersCapital() {
        FinancialAccounting accounting = new FinancialAccounting();
        accounting.setBaseCurrency(Currency.getInstance("CHF"));
        accounting.book("Sell products", LocalDate.of(2020, 1, 15), LocalDate.of(2020, 1, 17), new DebitCreditAmount(FinancialAccounting.BANK, FinancialAccounting.PRODUCT_REVENUES, Money.of("CHF", 1000000)));
        accounting.book("Pay salaries", LocalDate.of(2020, 1, 15), LocalDate.of(2020, 1, 17), new DebitCreditAmount(FinancialAccounting.SALARIES, FinancialAccounting.BANK, Money.of("CHF", 150000)));
        assertEquals(Money.of("CHF", 850000), accounting.getPnL(LocalDate.of(2020, 1, 18)));
    }

    @Test
    public void testPnL() {
        FinancialAccounting accounting = new FinancialAccounting();
        accounting.setBaseCurrency(Currency.getInstance("CHF"));
        accounting.book("Sell products", LocalDate.of(2020, 1, 15), LocalDate.of(2020, 1, 17), new DebitCreditAmount(FinancialAccounting.BANK, FinancialAccounting.PRODUCT_REVENUES, Money.of("CHF", 1000000)));
        accounting.book("Buy property", LocalDate.of(2020, 1, 15), LocalDate.of(2020, 1, 17), new DebitCreditAmount(FinancialAccounting.REAL_ESTATE, FinancialAccounting.BANK, Money.of("CHF", 400000)));
        accounting.book("Get credit", LocalDate.of(2020, 1, 15), LocalDate.of(2020, 1, 17), new DebitCreditAmount(FinancialAccounting.BANK, FinancialAccounting.LONG_TERM_DEBT, Money.of("CHF", 100000)));
        assertEquals(Money.of("CHF", 1000000), accounting.getOwnersCapital(LocalDate.of(2020, 1, 17)));
    }

    @Test
    public void testWrongCurrencyBooking() {
        FinancialAccounting accounting = new FinancialAccounting();
        accounting.setBaseCurrency(Currency.getInstance("CHF"));
        assertThrows(AssertionError.class, () -> accounting.book("Wrong Currency", LocalDate.of(2020, 1, 15), LocalDate.of(2020, 1, 17), new DebitCreditAmount(FinancialAccounting.BANK, FinancialAccounting.INTEREST, Money.of("USD", 1000000))));
    }
}
