package com.jore.epoc.mapper;

import com.jore.epoc.bo.EpocSetting;
import com.jore.epoc.dto.EpocSettingDto;

public interface SettingMapper {
    SettingMapper INSTANCE = new SettingMapper() {
        @Override
        public EpocSetting settingDtoToSetting(EpocSettingDto settingDto) {
            EpocSetting result = new EpocSetting();
            result.setId(settingDto.getId());
            result.setSettingKey(settingDto.getSettingKey());
            updateSettingFromSettingDto(result, settingDto);
            return result;
        }

        @Override
        public void updateSettingFromSettingDto(EpocSetting setting, EpocSettingDto settingDto) {
            setting.setDescription(settingDto.getDescription());
            setting.setSettingFormat(settingDto.getSettingFormat());
            setting.setValueText(settingDto.getValueText());
        }
    };

    EpocSetting settingDtoToSetting(EpocSettingDto settingDto);

    void updateSettingFromSettingDto(EpocSetting setting, EpocSettingDto settingDto);
}
