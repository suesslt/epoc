package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.jore.datatypes.percent.Percent;
import com.jore.epoc.bo.PercentDiscountFactor;

class DiscountFactorTest {
    @Test
    void testBelowOne() {
        PercentDiscountFactor discountFactor = new PercentDiscountFactor();
        discountFactor.setDiscountRate(Percent.of("10%"));
        assertThrows(AssertionError.class, () -> discountFactor.discount(0.9));
    }

    @Test
    void testEqualsOne() {
        PercentDiscountFactor discountFactor = new PercentDiscountFactor();
        discountFactor.setDiscountRate(Percent.of("10%"));
        assertEquals(1.0d, discountFactor.discount(1.0d));
    }

    @Test
    void testPositiveCase() {
        PercentDiscountFactor discountFactor = new PercentDiscountFactor();
        discountFactor.setDiscountRate(Percent.of("10%"));
        assertEquals(1.45d, discountFactor.discount(1.5d));
    }
}
