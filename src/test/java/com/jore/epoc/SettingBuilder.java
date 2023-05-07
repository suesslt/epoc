package com.jore.epoc;

import com.jore.epoc.bo.settings.EpocSetting;

public class SettingBuilder {
    public static SettingBuilder builder() {
        return new SettingBuilder();
    }

    private String valueText;
    private String settingKey;
    private String settingFormat;

    public SettingBuilder settingFormat(String settingFormat) {
        this.settingFormat = settingFormat;
        return this;
    }

    public SettingBuilder settingKey(String settingKey) {
        this.settingKey = settingKey;
        return this;
    }

    public SettingBuilder valueText(String valueText) {
        this.valueText = valueText;
        return this;
    }

    EpocSetting build() {
        EpocSetting result = new EpocSetting();
        result.setValueText(valueText);
        result.setSettingKey(settingKey);
        result.setSettingFormat(settingFormat);
        return result;
    }
}
