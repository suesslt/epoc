package com.jore.epoc.bo.accounting;

public class StorageCostBookingEvent extends AbstractBookingEvent {
    @Override
    public void book(Accounting accounting) {
        int creditBooking = accounting.loss("3210", getAmount());
        int debitBooking = accounting.debit("1020", getAmount());
        accounting.journal(creditBooking, debitBooking, getBookingDate(), getBookingText());
    }
}
