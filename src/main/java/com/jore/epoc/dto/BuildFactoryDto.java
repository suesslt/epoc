package com.jore.epoc.dto;

import com.jore.jpa.DataTransferObject;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BuildFactoryDto implements DataTransferObject {
    private int productionLines;
}
