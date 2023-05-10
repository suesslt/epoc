package com.jore.epoc.bo;

import com.jore.Assert;
import com.jore.datatypes.percent.Percent;

public class PercentDiscountFactor {
    private Percent discountRate;

    public double discount(double factor) {
        Assert.isTrue("Factor must be greater equal one (>=1.0)", factor >= 1.0);
        double result = factor;
        result -= 1.0d;
        result *= (1.0d - discountRate.doubleValue());
        result += 1.0d;
        return result;
    }

    public PercentDiscountFactor setDiscountRate(Percent discountRate) {
        this.discountRate = discountRate;
        return this;
    }
}
