package com.jore.epoc.bo.accounting;

import java.time.LocalDate;

public record BookingRecord(LocalDate bookingDate, String bookingText, DebitCreditAmount amount) {
}
