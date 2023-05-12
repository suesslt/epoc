package com.jore.epoc.dto;

import java.time.YearMonth;

import com.jore.datatypes.money.Money;
import com.jore.jpa.DataTransferObject;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EnterMarketDto implements DataTransferObject {
    @NotNull
    private Integer companySimulationStepId;
    @NotNull
    private Integer marketId;
    @NotNull
    private YearMonth executionMonth;
    @NotNull
    private int intentedProductSales;
    @NotNull
    private Money offeredPrice;
}
