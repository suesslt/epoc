package com.jore.epoc.bo;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

import com.jore.datatypes.money.Money;
import com.jore.datatypes.percent.Percent;

public class EpocSettings {
    private static EpocSettings INSTANCE = null;
    public static final String FACTORY_FIXED_COSTS = "Fixed costs per factory";
    public static final String FACTORY_VARIABLE_COSTS = "Variable costs per factory";
    public static final String MONTHLY_CAPACITY_PER_PRODUCTION_LINE = "Monthly capacity per production line";
    public static final String START_MONTH = "Default simulation start month";
    public static final String STORAGE_FIXED_COSTS = "Fixed costs per storage building";
    public static final String STORAGE_VARIABLE_COSTS = "Variable costs per storage";
    public static final String FACTORY_CREATION_MONTHS = "Months required to build a factory";
    public static final String STORAGE_CREATION_MONTHS = "Months required to build a storage";
    public static final String PASSWORD_LENGTH = "Length of password in characters";
    public static final String UNIT_PRODUCTION_COST = "Unit production cost";
    public static final String UNIT_LABOUR_COST = "Unit labour cost";
    public static final String STORAGE_COST_PER_UNIT_AND_MONTH = "Storage cost per unit and month";
    public static final String CREDIT_LINE_INTEREST_RATE = "Credit line interest rate";
    public static final String RAW_MATERIAL_UNIT_PRICE = "Unit price for raw material";

    public static EpocSettings getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EpocSettings();
            INSTANCE.initialize();
        }
        return INSTANCE;
    }

    private Map<String, Object> settings = new HashMap<>();

    public Object get(String key) {
        return settings.get(key);
    }

    private void initialize() {
        settings.put(FACTORY_FIXED_COSTS, Money.of("CHF", 1000000));
        settings.put(FACTORY_VARIABLE_COSTS, Money.of("CHF", 100000));
        settings.put(STORAGE_FIXED_COSTS, Money.of("CHF", 1000000));
        settings.put(STORAGE_VARIABLE_COSTS, Money.of("CHF", 1000));
        settings.put(MONTHLY_CAPACITY_PER_PRODUCTION_LINE, Integer.valueOf(100));
        settings.put(FACTORY_CREATION_MONTHS, Integer.valueOf(1));
        settings.put(STORAGE_CREATION_MONTHS, Integer.valueOf(1));
        settings.put(PASSWORD_LENGTH, Integer.valueOf(12));
        settings.put(START_MONTH, YearMonth.of(2000, 1));
        settings.put(UNIT_PRODUCTION_COST, Money.of("CHF", 10));
        settings.put(UNIT_LABOUR_COST, Money.of("CHF", 20));
        settings.put(STORAGE_COST_PER_UNIT_AND_MONTH, Money.of("CHF", 1.20));
        settings.put(CREDIT_LINE_INTEREST_RATE, Percent.of("5%"));
        settings.put(RAW_MATERIAL_UNIT_PRICE, Money.of("CHF", 35));
    }
}
