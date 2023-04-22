package com.jore.epoc.dto;

import com.jore.datatypes.money.Money;
import com.jore.jpa.DataTransferObject;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EnterMarketDto implements DataTransferObject {
    private Integer marketId;
    private Integer intentedProductSales;
    private Money offeredPrice;
}
