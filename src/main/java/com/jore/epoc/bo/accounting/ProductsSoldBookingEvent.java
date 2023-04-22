package com.jore.epoc.bo.accounting;

public class ProductsSoldBookingEvent extends AbstractBookingEvent {
    @Override
    public void book(Accounting accounting) {
        int creditBooking = accounting.profit("6200", getAmount());
        int debitBooking = accounting.debit("1080", getAmount());
        accounting.journal(creditBooking, debitBooking, getBookingDate(), getBookingText());
    }
}
