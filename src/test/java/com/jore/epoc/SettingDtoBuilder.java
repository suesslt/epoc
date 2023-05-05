package com.jore.epoc;

import com.jore.epoc.dto.SettingDto;

public class SettingDtoBuilder {
    public static SettingDtoBuilder builder() {
        return new SettingDtoBuilder();
    }

    public SettingDto build() {
        SettingDto result = new SettingDto();
        return result;
    }
}
