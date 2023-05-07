package com.jore.epoc.bo.settings;

import java.time.YearMonth;

import com.jore.datatypes.currency.Currency;
import com.jore.datatypes.money.Money;
import com.jore.datatypes.percent.Percent;
import com.jore.jpa.BusinessObject;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class EpocSetting extends BusinessObject {
    private String settingKey;
    private String settingFormat;
    private String valueText;
    private String description;
    @ManyToOne(optional = false)
    private EpocSettings settings;

    public Currency asCurrency() {
        return Currency.getInstance(valueText);
    }

    public Integer asInteger() {
        return Integer.valueOf(valueText);
    }

    public Money asMoney() {
        return Money.parse(valueText);
    }

    public Percent asPercent() {
        return Percent.parse(valueText);
    }

    public YearMonth asYearMonth() {
        return YearMonth.parse(valueText);
    }

    public EpocSetting copyWithoutId() {
        EpocSetting result = new EpocSetting();
        result.setSettingKey(settingKey);
        result.setSettingFormat(settingFormat);
        result.setValueText(valueText);
        result.setDescription(description);
        return result;
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

    public void setSettings(EpocSettings settings) {
        this.settings = settings;
    }

    public void setValueText(String valueText) {
        this.valueText = valueText;
    }

    @Override
    public String toString() {
        return "EpocSetting [settingKey=" + settingKey + ", settingFormat=" + settingFormat + ", valueText=" + valueText + ", description=" + description + "]";
    }
}
