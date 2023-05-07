package com.jore.epoc.dto;

import java.time.YearMonth;

import com.jore.epoc.bo.message.MessageLevel;
import com.jore.jpa.DataTransferObject;

import lombok.Data;

@Data
public class MessageDto implements DataTransferObject {
    private MessageLevel level;
    private String message;
    private YearMonth relevantMonth;
}
