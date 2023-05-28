package com.jore.epoc.dto;

import java.time.YearMonth;

import com.jore.datatypes.money.Money;
import com.jore.jpa.DataTransferObject;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RunMarketingCampaignDto implements DataTransferObject {
    @NotNull
    private Long companyId;
    @NotNull
    private Money campaignAmount;
    @NotNull
    private YearMonth executionMonth;
}
