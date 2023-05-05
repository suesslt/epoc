package com.jore.epoc.dto;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import com.jore.jpa.DataTransferObject;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimulationDto implements DataTransferObject {
    private Integer id;
    private String name;
    @Builder.Default
    private List<CompanyDto> companies = new ArrayList<>();
    @Builder.Default
    private List<SettingDto> settings = new ArrayList<>();
    private boolean isStarted;
    private YearMonth startMonth;
    private Integer nrOfSteps;
    private boolean isFinished;

    public void addCompany(CompanyDto companyDto) {
        companies.add(companyDto);
    }

    public void addSetting(SettingDto settingDto) {
        settings.add(settingDto);
    }
}
