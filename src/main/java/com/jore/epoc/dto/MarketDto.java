package com.jore.epoc.dto;

import java.math.BigDecimal;

import com.jore.datatypes.money.Money;
import com.jore.datatypes.percent.Percent;
import com.jore.jpa.DataTransferObject;

import lombok.Data;

@Data
public class MarketDto implements DataTransferObject {
    private Integer id;
    private String name;
    private Money gdpPpp;
    private Money gdp;
    private Percent gdpGrowth;
    private int laborForce;
    private Percent unemployment;
    private BigDecimal lifeExpectancy;
    private Money costToEnterMarket;
    private int ageTo14Male;
    private int ageTo14Female;
    private int ageTo24Male;
    private int ageTo24Female;
    private int ageTo54Male;
    private int ageTo54Female;
    private int ageTo64Male;
    private int ageTo64Female;
    private int age65olderMale;
    private int age65olderFemale;
    private int requiredSalesforce;
    private Money distributionCost;
}
