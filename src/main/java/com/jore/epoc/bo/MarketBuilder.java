package com.jore.epoc.bo;

import com.jore.datatypes.money.Money;

public class MarketBuilder {
    public static MarketBuilder builder() {
        return new MarketBuilder();
    }

    private String name;
    private Money costToEnterMarket;

    public Market build() {
        Market result = new Market();
        result.setName(name);
        result.setCostToEnterMarket(costToEnterMarket);
        return result;
    }

    public MarketBuilder costToEnterMarket(Money costToEnterMarket) {
        this.costToEnterMarket = costToEnterMarket;
        return this;
    }

    public MarketBuilder name(String name) {
        this.name = name;
        return this;
    }
}
