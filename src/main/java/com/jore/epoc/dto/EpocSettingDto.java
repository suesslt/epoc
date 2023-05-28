package com.jore.epoc.dto;

import lombok.Data;

@Data
public class EpocSettingDto {
    private Long id;
    private String settingKey;
    private String settingFormat;
    private String valueText;
    private String description;
}
