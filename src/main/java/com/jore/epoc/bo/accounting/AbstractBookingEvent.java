package com.jore.epoc.bo.accounting;

import java.time.LocalDate;

import com.jore.datatypes.money.Money;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class AbstractBookingEvent implements BookingEvent {
    private String bookingText;
    private LocalDate bookingDate;
    private Money amount;
}
