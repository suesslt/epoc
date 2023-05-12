package com.jore.epoc.dto;

import java.time.YearMonth;

import com.jore.datatypes.money.Money;
import com.jore.jpa.DataTransferObject;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdjustCreditLineDto implements DataTransferObject {
    @NotNull
    private Integer companySimulationStepId;
    @NotNull
    private YearMonth executionMonth;
    @NotNull
    private Money amount;
}
