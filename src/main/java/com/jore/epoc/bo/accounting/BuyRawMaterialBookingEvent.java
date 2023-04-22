package com.jore.epoc.bo.accounting;

public class BuyRawMaterialBookingEvent extends AbstractBookingEvent {
    @Override
    public void book(Accounting accounting) {
        int creditBooking = accounting.credit("1072", getAmount());
        int debitBooking = accounting.debit("1020", getAmount());
        accounting.journal(creditBooking, debitBooking, getBookingDate(), getBookingText());
    }
}
