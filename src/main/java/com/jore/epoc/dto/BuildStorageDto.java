package com.jore.epoc.dto;

import java.time.YearMonth;

import com.jore.jpa.DataTransferObject;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BuildStorageDto implements DataTransferObject {
    @NotNull
    private Integer companySimulationStepId;
    @Min(1)
    @Max(1000)
    private int capacity;
    @NotNull
    private YearMonth executionMonth;
}
