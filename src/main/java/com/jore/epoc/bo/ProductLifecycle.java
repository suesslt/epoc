package com.jore.epoc.bo;

import com.jore.Assert;
import com.jore.util.NormalDistribution;

public class ProductLifecycle {
    private final static NormalDistribution NORMAL_DISTRIBUTION = new NormalDistribution();
    private final static double STANDARD_DEVIATION_SPAN = 4.6d;
    private final static double TAIL_ERROR = 0.01072411002d;
    private final double productLifecycleDuration;

    public ProductLifecycle(double d) {
        this.productLifecycleDuration = d;
    }

    public double getPercentageSoldForMonths(int months) {
        Assert.isTrue("Duration not initialized", productLifecycleDuration > 0);
        final double durationFraction = months / productLifecycleDuration;
        final double standardDeviationFraction = -(STANDARD_DEVIATION_SPAN / 2) + (durationFraction * STANDARD_DEVIATION_SPAN);
        double percentageSold = NORMAL_DISTRIBUTION.distributionFor(standardDeviationFraction) - TAIL_ERROR;
        percentageSold += (percentageSold * TAIL_ERROR * 2);
        return Math.max(Math.min(percentageSold, 100), 0);
    }
}
