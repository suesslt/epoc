package com.jore.epoc.dto;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import com.jore.jpa.DataTransferObject;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SimulationDto implements DataTransferObject {
    private Integer id;
    private String name;
    @Builder.Default
    private List<CompanyDto> companies = new ArrayList<CompanyDto>();
    private boolean isStarted;
    private YearMonth startMonth;
    private int nrOfSteps;

    public void addCompany(CompanyDto companyDto) {
        companies.add(companyDto);
    }
}
