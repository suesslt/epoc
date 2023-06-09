package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.jore.datatypes.money.Money;
import com.jore.datatypes.percent.Percent;
import com.jore.epoc.bo.DemandCurve;

public class DemandCurveTest {
    DemandCurve demandCurve = DemandCurve.create("USD", 1000, "20%", 200, "80%");

    @Test
    public void testFailedInTestExample() {
        DemandCurve myCurve = DemandCurve.create("CHF", 1200, "20%", 500, "80%");
        assertEquals(Percent.of("54.2857%"), myCurve.getDemandForPrice(Money.of("CHF", 800)));
    }

    @Test
    public void testGetDemandForPriceOf1000() {
        assertEquals(Percent.of("20%"), demandCurve.getDemandForPrice(Money.of("USD", 1000)));
    }

    @Test
    public void testGetDemandForPriceOf200() {
        assertEquals(Percent.of("80%"), demandCurve.getDemandForPrice(Money.of("USD", 200)));
    }

    @Test
    public void testGetDemandForPriceOf2000() {
        assertEquals(Percent.of("0%"), demandCurve.getDemandForPrice(Money.of("USD", 2000)));
    }

    @Test
    public void testGetDemandForPriceOf600() {
        assertEquals(Percent.of("50%"), demandCurve.getDemandForPrice(Money.of("USD", 600)));
    }

    @Test
    public void testGetDemandForPriceOfNegative1000() {
        assertEquals(Percent.of("95%"), demandCurve.getDemandForPrice(Money.of("USD", -1000)));
    }

    @Test
    public void testGetDemandForPriceOfZero() {
        assertEquals(Percent.of("95%"), demandCurve.getDemandForPrice(Money.of("USD", 0)));
    }
}
