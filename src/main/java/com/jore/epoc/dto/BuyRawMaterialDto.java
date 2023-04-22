package com.jore.epoc.dto;

import com.jore.jpa.DataTransferObject;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BuyRawMaterialDto implements DataTransferObject {
    private Integer amount;
}
