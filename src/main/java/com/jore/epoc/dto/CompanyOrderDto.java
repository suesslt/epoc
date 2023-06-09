package com.jore.epoc.dto;

import com.jore.datatypes.money.Money;
import com.jore.jpa.DataTransferObject;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyOrderDto implements DataTransferObject {
    private String orderType;
    private Money amount;
}
