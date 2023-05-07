package com.jore.epoc.dto;

public class SettingDto {
    private String settingKey;
    private String valueText;

    public String getSettingKey() {
        return settingKey;
    }

    public String getValueText() {
        return valueText;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public void setValueText(String valueText) {
        this.valueText = valueText;
    }
}
