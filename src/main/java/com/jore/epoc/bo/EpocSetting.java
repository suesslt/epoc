package com.jore.epoc.bo;

import com.jore.jpa.BusinessObject;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
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
    public static final String STORAGE_COST_PER_UNIT_AND_MONTH = "SET0012";
    public static final String CREDIT_LINE_INTEREST_RATE = "SET0013";
    public static final String RAW_MATERIAL_UNIT_PRICE = "SET0014";
    private String settingKey;
    private String settingFormat;
    private String valueText;
    private String description;
}
