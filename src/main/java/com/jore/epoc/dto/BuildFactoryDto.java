package com.jore.epoc.dto;

import java.time.YearMonth;

import com.jore.jpa.DataTransferObject;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BuildFactoryDto implements DataTransferObject {
    @NotNull
    private Integer companySimulationStepId;
    @NotNull
    private YearMonth executionMonth;
    @Min(1)
    @Max(10)
    private int productionLines;
}
