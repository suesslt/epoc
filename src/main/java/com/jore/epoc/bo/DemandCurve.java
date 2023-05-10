package com.jore.epoc.bo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

import com.jore.Assert;
import com.jore.datatypes.money.Money;
import com.jore.datatypes.percent.Percent;

// TODO do not take amount out of money!
public class DemandCurve {
    public static DemandCurve create(String currency, double higherPrice, String higherPricePercent, double lowerPrice, String lowerPricePercent) {
        return new DemandCurve(Money.of(currency, higherPrice), Percent.of(higherPricePercent), Money.of(currency, lowerPrice), Percent.of(lowerPricePercent));
    }

    private final Percent lowerPricePercent;
    private final Money lowerPrice;
    private final Percent higherPricePercent;
    private final Money higherPrice;

    public DemandCurve(Money higherPrice, Percent higherPricePercent, Money lowerPrice, Percent lowerPricePercent) {
        super();
        this.lowerPricePercent = Objects.requireNonNull(lowerPricePercent, "Lower price percent must not be null");
        this.lowerPrice = Objects.requireNonNull(lowerPrice, "Lower price must not be null");
        this.higherPricePercent = Objects.requireNonNull(higherPricePercent, "Higher price percent must not be null");
        this.higherPrice = Objects.requireNonNull(higherPrice, "Higher price must not be null");
        Assert.isTrue("Lower price must be lower than higher price", lowerPrice.compareTo(higherPrice) < 0);
    }

    public Percent getDemandForPrice(Money price) {
        final Money priceDifference = higherPrice.subtract(lowerPrice);
        final Percent percentDifference = higherPricePercent.subtract(lowerPricePercent);
        final BigDecimal aValue = percentDifference.getFactorAmount().divide(priceDifference.getAmount(), new MathContext(Percent.SCALE, RoundingMode.HALF_UP));
        final BigDecimal bValue = lowerPricePercent.getFactorAmount().subtract(aValue.multiply(lowerPrice.getAmount()));
        return Percent.of(bValue.min(Objects.requireNonNull(price, "Price must not be null").getAmount().multiply(aValue).add(bValue).max(BigDecimal.ZERO)));
    }
}
