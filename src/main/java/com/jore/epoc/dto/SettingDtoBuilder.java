package com.jore.epoc.dto;

public class SettingDtoBuilder {
    public static SettingDtoBuilder builder() {
        return new SettingDtoBuilder();
    }

    private String settingKey;
    private String valueText;

    public SettingDto build() {
        SettingDto result = new SettingDto();
        result.setSettingKey(settingKey);
        result.setValueText(valueText);
        return result;
    }

    public SettingDtoBuilder settingKey(String settingKey) {
        this.settingKey = settingKey;
        return this;
    }

    public SettingDtoBuilder valueText(String valueText) {
        this.valueText = valueText;
        return this;
    }
}
