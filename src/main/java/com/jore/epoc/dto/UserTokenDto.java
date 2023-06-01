package com.jore.epoc.dto;

import com.jore.jpa.DataTransferObject;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserTokenDto implements DataTransferObject {
    private Long userId;
    private Long userTokenId;
}
