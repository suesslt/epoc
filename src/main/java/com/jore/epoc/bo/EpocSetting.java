package com.jore.epoc.bo;

import com.jore.jpa.BusinessObject;

import jakarta.persistence.Entity;

@Entity
public class EpocSetting extends BusinessObject {
    public static final String FACTORY_FIXED_COSTS = "SET0001";
    public static final String FACTORY_VARIABLE_COSTS = "SET0002";
    public static final String STORAGE_FIXED_COSTS = "SET0003";
    public static final String STORAGE_VARIABLE_COSTS = "SET0004";
    public static final String MONTHLY_CAPACITY_PER_PRODUCTION_LINE = "SET0005";
    public static final String FACTORY_CREATION_MONTHS = "SET0006";
    public static final String STORAGE_CREATION_MONTHS = "SET0007";
    public static final String PASSWORD_LENGTH = "SET0008";
    public static final String START_MONTH = "SET0009";
    public static final String UNIT_PRODUCTION_COST = "SET0010";
    public static final String UNIT_LABOUR_COST = "SET0011";
    public static final String BUILDING_MAINTENANCE = "SET0012";
    public static final String CREDIT_LINE_INTEREST_RATE = "SET0013";
    public static final String RAW_MATERIAL_UNIT_PRICE = "SET0014";
    public static final String DEMAND_HIGHER_PERCENT = "SET0015";
    public static final String DEMAND_HIGHER_PRICE = "SET0016";
    public static final String DEMAND_LOWER_PERCENT = "SET0017";
    public static final String DEMAND_LOWER_PRICE = "SET0018";
    public static final String PRODUCT_LIFECYCLE_DURATION = "SET0019";
    public static final String DISTRIBUTION_FIXED_COSTS = "SET0020";
    public static final String DISTRIBUTION_VARIABLE_COSTS = "SET0021";
    public static final String BASE_CURRENCY = "SET0022";
    public static final String DEPRECIATION_RATE = "SET0023";
    public static final String INVENTORY_MANAGEMENT_COST = "SET0024";
    public static final String HEADQUARTER_COST = "SET0025";
    private String settingKey;
    private String settingFormat;
    private String valueText;
    private String description;

    public String getDescription() {
        return description;
    }

    public String getSettingFormat() {
        return settingFormat;
    }

    public String getSettingKey() {
        return settingKey;
    }

    public String getValueText() {
        return valueText;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSettingFormat(String settingFormat) {
        this.settingFormat = settingFormat;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public void setValueText(String valueText) {
        this.valueText = valueText;
    }

    @Override
    public String toString() {
        return "EpocSetting [settingKey=" + settingKey + ", settingFormat=" + settingFormat + ", valueText=" + valueText + ", description=" + description + "]";
    }
}
