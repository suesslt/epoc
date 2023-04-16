package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.CreditEventDirection;
import com.jore.epoc.bo.CreditLine;

class CreditLineTests {
    @Test
    void testDecreaseCreditLineBelowZero() {
        CreditLine creditLine = new CreditLine();
        creditLine.setCreditAmount(Money.of("CHF", 100000));
        assertEquals(Money.of("CHF", 100000), creditLine.getCreditAmount());
        creditLine.adjustAmount(CreditEventDirection.DECREASE, Money.of("CHF", 500000));
        assertEquals(Money.of("CHF", 0), creditLine.getCreditAmount());
    }

    @Test
    void testIncreaseCreditLine() {
        CreditLine creditLine = new CreditLine();
        assertNull(creditLine.getCreditAmount());
        creditLine.adjustAmount(CreditEventDirection.INCREASE, Money.of("CHF", 1000000));
        assertEquals(Money.of("CHF", 1000000), creditLine.getCreditAmount());
    }

    @Test
    void testNewCreditLine() {
        CreditLine creditLine = new CreditLine();
        assertNull(creditLine.getCreditAmount());
    }
}
