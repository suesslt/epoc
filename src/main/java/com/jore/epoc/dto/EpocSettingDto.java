package com.jore.epoc.dto;

import lombok.Data;

@Data
public class EpocSettingDto {
    private String settingKey;
    private String settingFormat;
    private String valueText;
    private String description;
    private Integer id;
}
