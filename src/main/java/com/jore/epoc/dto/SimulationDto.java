package com.jore.epoc.dto;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import com.jore.datatypes.currency.Currency;
import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.SimulationType;
import com.jore.jpa.DataTransferObject;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimulationDto implements DataTransferObject {
    @NotNull
    private Long id;
    @NotEmpty
    private String name;
    @Builder.Default
    private List<CompanyDto> companies = new ArrayList<>();
    @Builder.Default
    private List<SettingDto> settings = new ArrayList<>();
    private boolean isStarted;
    @NotNull
    private YearMonth startMonth;
    @NotNull
    @Min(1)
    private Integer nrOfMonths;
    private boolean isFinished;
    private Currency baseCurrency;
    private Money costToBuildFactory;
    private SimulationType simulationType;

    public void addCompany(CompanyDto companyDto) {
        companies.add(companyDto);
    }

    public void addSetting(SettingDto settingDto) {
        settings.add(settingDto);
    }

    public Integer getNrOfCompanies() {
        return companies.size();
    }
}
