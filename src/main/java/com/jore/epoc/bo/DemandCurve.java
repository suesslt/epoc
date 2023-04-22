package com.jore.epoc.bo;

import java.math.BigDecimal;
import java.util.Objects;

import com.jore.Assert;
import com.jore.datatypes.currency.Currency;
import com.jore.datatypes.money.Money;
import com.jore.datatypes.percent.Percent;

public class DemandCurve {
    public static DemandCurve create(Currency demandCurveCurrency, BigDecimal demandCurveHigherPrice, Percent demandCurveHigherPricePercent, BigDecimal demandCurveLowerPrice, Percent demandCurveLowerPricePercent) {
        return new DemandCurve(Money.of(demandCurveCurrency, demandCurveHigherPrice), demandCurveHigherPricePercent, Money.of(demandCurveCurrency, demandCurveLowerPrice), demandCurveLowerPricePercent);
    }

    public static DemandCurve create(String currency, double higherPrice, String higherPricePercent, double lowerPrice, String lowerPricePercent) {
        return new DemandCurve(Money.of(currency, higherPrice), new Percent(higherPricePercent), Money.of(currency, lowerPrice), new Percent(lowerPricePercent));
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
        final BigDecimal aValue = percentDifference.getFactorAmount().divide(priceDifference.getAmount());
        final BigDecimal bValue = lowerPricePercent.getFactorAmount().subtract(aValue.multiply(lowerPrice.getAmount()));
        return new Percent(bValue.min(Objects.requireNonNull(price, "Price must not be null").getAmount().multiply(aValue).add(bValue).max(BigDecimal.ZERO)));
    }
}
