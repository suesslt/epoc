package com.jore.epoc.bo.settings;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

import com.jore.datatypes.currency.Currency;
import com.jore.datatypes.money.Money;
import com.jore.datatypes.percent.Percent;
import com.jore.epoc.bo.SimulationType;
import com.jore.jpa.AbstractBusinessObject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;

@Entity
public class EpocSettings extends AbstractBusinessObject {
    public static final String FACTORY_FIXED_COST = "SET0001";
    public static final String FACTORY_COST_PER_PRODUCTION_LINE = "SET0002";
    public static final String STORAGE_FIXED_COST = "SET0003";
    public static final String STORAGE_VARIABLE_COST_PER_SLOT = "SET0004";
    public static final String DAILY_CAPACITY_PER_PRODUCTION_LINE = "SET0005";
    public static final String FACTORY_CONSTRUCTION_MONTHS = "SET0006";
    public static final String STORAGE_CONSTRUCTION_MONTHS = "SET0007";
    public static final String PASSWORD_LENGTH = "SET0008";
    public static final String DEFAULT_SIMULATION_START_MONTH = "SET0009";
    public static final String SIMULATION_TYPE = "SET0010";
    public static final String LABOR_COST_PER_PRODUCTION_LINE = "SET0011";
    public static final String MAINTENANCE_COST_PER_BUILDING = "SET0012";
    public static final String DEBT_INTEREST_RATE = "SET0013";
    public static final String RAW_MATERIAL_PURCHASE_PRICE = "SET0014";
    public static final String DEMAND_HIGHER_PERCENT = "SET0015";
    public static final String DEMAND_HIGHER_PRICE = "SET0016";
    public static final String DEMAND_LOWER_PERCENT = "SET0017";
    public static final String DEMAND_LOWER_PRICE = "SET0018";
    public static final String PRODUCT_LIFECYCLE_DURATION = "SET0019";
    public static final String MARKET_ENTRY_COST = "SET0020";
    public static final String BASE_CURRENCY = "SET0022";
    public static final String DEPRECIATION_RATE = "SET0023";
    public static final String INVENTORY_MANAGEMENT_COST = "SET0024";
    public static final String HEADQUARTER_COST = "SET0025";
    public static final String PRODUCTION_COST = "SET0026";
    public static final String PASSIVE_STEPS = "SET0027";
    public static final String PRICE_PER_POINT_QUALITY = "SET0028";
    public static final String PRICE_PER_MARKETING_CAMPAIGN = "SET0029";
    public static final String PRICE_PER_PRODUCTIVITY_POINT = "SET0030";
    public static final String FACTOR_DISCOUNT_RATE = "SET0031";
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "settings", orphanRemoval = true)
    @MapKey(name = "settingKey")
    private Map<String, EpocSetting> settings = new HashMap<>();
    private boolean isTemplate;

    public void addSetting(EpocSetting setting) {
        setting.setSettings(this);
        settings.put(setting.getSettingKey(), setting);
    }

    public EpocSettings copyWithoutId() {
        EpocSettings result = new EpocSettings();
        result.setTemplate(isTemplate);
        for (EpocSetting setting : settings.values()) {
            result.addSetting(setting.copyWithoutId());
        }
        return result;
    }

    public Currency getBaseCurrency() {
        return settings.get(BASE_CURRENCY).asCurrency();
    }

    public Integer getDailyCapacityPerProductionLine() {
        return settings.get(DAILY_CAPACITY_PER_PRODUCTION_LINE).asInteger();
    }

    public Percent getDebtInterestRate() {
        return settings.get(DEBT_INTEREST_RATE).asPercent();
    }

    public Percent getDemandHigherPercent() {
        return settings.get(DEMAND_HIGHER_PERCENT).asPercent();
    }

    public Money getDemandHigherPrice() {
        return settings.get(DEMAND_HIGHER_PRICE).asMoney();
    }

    public Percent getDemandLowerPercent() {
        return settings.get(DEMAND_LOWER_PERCENT).asPercent();
    }

    public Money getDemandLowerPrice() {
        return settings.get(DEMAND_LOWER_PRICE).asMoney();
    }

    public Percent getDepreciationRate() {
        return settings.get(DEPRECIATION_RATE).asPercent();
    }

    public Percent getFactorDiscountRate() {
        return settings.get(FACTOR_DISCOUNT_RATE).asPercent();
    }

    public Money getFactoryConstructionCost() {
        return settings.get(FACTORY_FIXED_COST).asMoney();
    }

    public Money getFactoryConstructionCostsPerLine() {
        return settings.get(FACTORY_COST_PER_PRODUCTION_LINE).asMoney();
    }

    public Money getHeadquarterCost() {
        return settings.get(HEADQUARTER_COST).asMoney();
    }

    public Money getInventoryManagementCost() {
        return settings.get(INVENTORY_MANAGEMENT_COST).asMoney();
    }

    public Money getMaintentanceCostPerBuilding() {
        return settings.get(MAINTENANCE_COST_PER_BUILDING).asMoney();
    }

    public Integer getPassiveSteps() {
        return settings.get(PASSIVE_STEPS).asInteger();
    }

    public Integer getPasswordLength() {
        return settings.get(PASSWORD_LENGTH).asInteger();
    }

    public Money getPricePerMarketingCampaign() {
        return settings.get(PRICE_PER_MARKETING_CAMPAIGN).asMoney();
    }

    public Money getPricePerPercentPointProductivity() {
        return settings.get(PRICE_PER_PRODUCTIVITY_POINT).asMoney();
    }

    public Money getPricePerPercentPointQuality() {
        return settings.get(PRICE_PER_POINT_QUALITY).asMoney();
    }

    public Money getProductionCostPerProduct() {
        return settings.get(PRODUCTION_COST).asMoney();
    }

    public Money getProductionLineLaborCost() {
        return settings.get(LABOR_COST_PER_PRODUCTION_LINE).asMoney();
    }

    public int getProductLifecycleDuration() {
        return settings.get(PRODUCT_LIFECYCLE_DURATION).asInteger();
    }

    public Money getRawMaterialUnitPrice() {
        return settings.get(RAW_MATERIAL_PURCHASE_PRICE).asMoney();
    }

    public EpocSetting getSettingByKey(String settingKey) {
        return settings.get(settingKey);
    }

    public YearMonth getSimulationStartMonth() {
        return settings.get(DEFAULT_SIMULATION_START_MONTH).asYearMonth();
    }

    public SimulationType getSimulationType() {
        return SimulationType.valueOf(settings.get(SIMULATION_TYPE).asString());
    }

    public Integer getStorageConstructionMonths() {
        return settings.get(STORAGE_CONSTRUCTION_MONTHS).asInteger();
    }

    public Money getStorageCostPerUnit() {
        return settings.get(STORAGE_VARIABLE_COST_PER_SLOT).asMoney();
    }

    public Money getStorageFixedCost() {
        return settings.get(STORAGE_FIXED_COST).asMoney();
    }

    public Integer getTimeToBuild() {
        return settings.get(FACTORY_CONSTRUCTION_MONTHS).asInteger();
    }

    public boolean isTemplate() {
        return isTemplate;
    }

    public void setTemplate(boolean isTemplate) {
        this.isTemplate = isTemplate;
    }
}
