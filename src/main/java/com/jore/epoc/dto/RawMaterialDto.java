package com.jore.epoc.dto;

import com.jore.jpa.DataTransferObject;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RawMaterialDto implements DataTransferObject {
    private Integer amount;
}
