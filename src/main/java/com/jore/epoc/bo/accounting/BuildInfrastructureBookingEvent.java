package com.jore.epoc.bo.accounting;

public class BuildInfrastructureBookingEvent extends AbstractBookingEvent {
    @Override
    public void book(Accounting accounting) {
        int creditBooking = accounting.credit("1100", getAmount());
        int debitBooking = accounting.debit("1020", getAmount());
        accounting.journal(creditBooking, debitBooking, getBookingDate(), getBookingText());
    }
}
