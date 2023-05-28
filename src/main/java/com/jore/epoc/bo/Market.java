package com.jore.epoc.bo;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Arrays;

import org.hibernate.annotations.CompositeType;
import org.hibernate.annotations.Type;

import com.jore.Assert;
import com.jore.datatypes.money.Money;
import com.jore.datatypes.percent.Percent;
import com.jore.jpa.BusinessObject;
import com.jore.util.Util;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

@Entity
public class Market extends BusinessObject {
    private String name;
    @AttributeOverride(name = "amount", column = @Column(name = "gdp_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "gdp_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money gdpPpp;
    @AttributeOverride(name = "amount", column = @Column(name = "gdp_ppp_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "gdp_ppp_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money gdp;
    @Type(com.jore.datatypes.hibernate.PercentUserType.class)
    private Percent gdpGrowth;
    @Type(com.jore.datatypes.hibernate.PercentUserType.class)
    private Percent unemployment;
    private BigDecimal lifeExpectancy;
    private Integer laborForce;
    @AttributeOverride(name = "amount", column = @Column(name = "cost_to_enter_market_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "cost_to_enter_market_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money costToEnterMarket;
    @AttributeOverride(name = "amount", column = @Column(name = "distribution_cost_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "distribution_cost_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money distributionCost;
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
    @Transient
    private boolean ageTableUpdated = false;
    @Transient
    private int[] ageTableMale;
    @Transient
    private int[] ageTableFemale;

    public int calculateMarketPotential(YearMonth startMonth, YearMonth simulationMonth, Integer productLifecycleDuration) {
        ProductLifecycle productLifecycle = new ProductLifecycle(productLifecycleDuration);
        double percentageSold = productLifecycle.getPercentageSoldForMonths(Util.monthDiff(simulationMonth, startMonth));
        int marketSizeForConsumption = getMarketSizeForConsumption();
        return (int) (marketSizeForConsumption * percentageSold);
    }

    public Money getCostToEnterMarket() {
        return costToEnterMarket;
    }

    public Money getDistributionCost() {
        return distributionCost;
    }

    public int getFemalePopulation() {
        updateAgetable();
        int result = 0;
        for (int i = 0; i < ageTableFemale.length; i++) {
            result = result + ageTableFemale[i];
        }
        return result;
    }

    public Money getGdp() {
        return gdp;
    }

    public Percent getGdpGrowth() {
        return gdpGrowth;
    }

    public Money getGdpPpp() {
        return gdpPpp;
    }

    public Integer getLaborForce() {
        return laborForce;
    }

    public BigDecimal getLifeExpectancy() {
        return lifeExpectancy;
    }

    public int getMalePopulation() {
        updateAgetable();
        int result = 0;
        for (int i = 0; i < ageTableMale.length; i++) {
            result = result + ageTableMale[i];
        }
        return result;
    }

    public Integer getMarketSizeForConsumption() {
        Integer result = getLaborForce();
        return result;
    }

    public String getName() {
        return name;
    }

    public int getPopulationForAge(int age) {
        updateAgetable();
        return age < ageTableMale.length ? ageTableMale[age] + ageTableFemale[age] : 0;
    }

    public int getTotalPopulation() {
        updateAgetable();
        return getMalePopulation() + getFemalePopulation();
    }

    public Percent getUnemployment() {
        return unemployment;
    }

    public void setAge65olderFemale(int age65olderFemale) {
        ageTableUpdated = false;
        this.age65olderFemale = age65olderFemale;
    }

    public void setAge65olderMale(int age65olderMale) {
        ageTableUpdated = false;
        this.age65olderMale = age65olderMale;
    }

    public void setAgeTo14Female(int ageTo14Female) {
        ageTableUpdated = false;
        this.ageTo14Female = ageTo14Female;
    }

    public void setAgeTo14Male(int ageTo14Male) {
        ageTableUpdated = false;
        this.ageTo14Male = ageTo14Male;
    }

    public void setAgeTo24Female(int ageTo24Female) {
        ageTableUpdated = false;
        this.ageTo24Female = ageTo24Female;
    }

    public void setAgeTo24Male(int ageTo24Male) {
        ageTableUpdated = false;
        this.ageTo24Male = ageTo24Male;
    }

    public void setAgeTo54Female(int ageTo54Female) {
        ageTableUpdated = false;
        this.ageTo54Female = ageTo54Female;
    }

    public void setAgeTo54Male(int ageTo54Male) {
        ageTableUpdated = false;
        this.ageTo54Male = ageTo54Male;
    }

    public void setAgeTo64Female(int ageTo64Female) {
        ageTableUpdated = false;
        this.ageTo64Female = ageTo64Female;
    }

    public void setAgeTo64Male(int ageTo64Male) {
        ageTableUpdated = false;
        this.ageTo64Male = ageTo64Male;
    }

    public void setCostToEnterMarket(Money costToEnterMarket) {
        this.costToEnterMarket = costToEnterMarket;
    }

    public void setDistributionCost(Money distributionCost) {
        this.distributionCost = distributionCost;
    }

    public void setGdp(Money gdp) {
        this.gdp = gdp;
    }

    public void setGdpGrowth(Percent gdpGrowth) {
        this.gdpGrowth = gdpGrowth;
    }

    public void setGdpPpp(Money gdpPpp) {
        this.gdpPpp = gdpPpp;
    }

    public void setLaborForce(Integer laborForce) {
        this.laborForce = laborForce;
    }

    public void setLifeExpectancy(BigDecimal lifeExpectancy) {
        ageTableUpdated = false;
        this.lifeExpectancy = lifeExpectancy;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUnemployment(Percent unemployment) {
        this.unemployment = unemployment;
    }

    @Override
    public String toString() {
        return "Market [id=" + getId() + ", ageTo14Male=" + ageTo14Male + ", ageTo14Female=" + ageTo14Female + ", ageTo24Male=" + ageTo24Male + ", ageTo24Female=" + ageTo24Female + ", ageTo54Male=" + ageTo54Male + ", ageTo54Female=" + ageTo54Female + ", ageTo64Male=" + ageTo64Male
                + ", ageTo64Female=" + ageTo64Female + ", age65olderMale=" + age65olderMale + ", age65olderFemale=" + age65olderFemale + ", ageTableUpdated=" + ageTableUpdated + ", ageTableMale=" + Arrays.toString(ageTableMale) + ", ageTableFemale=" + Arrays.toString(ageTableFemale)
                + ", lifeExpectancy=" + lifeExpectancy + ", marketSizeForConsumption=" + getMarketSizeForConsumption() + ", unemploymentRate=" + unemployment + ", name=" + name + "]";
    }

    private int calculateForAgeOver65(int number, int divider, int maximumAge, int i) {
        return Math.round(number / divider * (maximumAge - 65 - (i - 65)));
    }

    private int calculateForYear(int number, int years) {
        return Math.round(number / years);
    }

    private void updateAgetable() {
        Assert.isTrue("Life Expectancy must be greater than 65", lifeExpectancy.intValue() > 65);
        if (!ageTableUpdated) {
            int maximumAge = lifeExpectancy.intValue() + (lifeExpectancy.intValue() - 65);
            ageTableMale = new int[maximumAge];
            ageTableFemale = new int[maximumAge];
            for (int i = 0; i < 15; i++) {
                ageTableMale[i] = calculateForYear(ageTo14Male, 15);
                ageTableFemale[i] = calculateForYear(ageTo14Female, 15);
            }
            for (int i = 15; i < 25; i++) {
                ageTableMale[i] = calculateForYear(ageTo24Male, 10);
                ageTableFemale[i] = calculateForYear(ageTo24Female, 10);
            }
            for (int i = 25; i < 55; i++) {
                ageTableMale[i] = calculateForYear(ageTo54Male, 30);
                ageTableFemale[i] = calculateForYear(ageTo54Female, 30);
            }
            for (int i = 55; i < 65; i++) {
                ageTableMale[i] = calculateForYear(ageTo64Male, 10);
                ageTableFemale[i] = calculateForYear(ageTo64Female, 10);
            }
            int divider = 0;
            for (int i = 0; i < (maximumAge - 65); i++) {
                divider += (i + 1);
            }
            for (int i = 65; i < maximumAge; i++) {
                ageTableMale[i] = calculateForAgeOver65(age65olderMale, divider, maximumAge, i);
                ageTableFemale[i] = calculateForAgeOver65(age65olderFemale, divider, maximumAge, i);
            }
            ageTableUpdated = true;
        }
    }
}
