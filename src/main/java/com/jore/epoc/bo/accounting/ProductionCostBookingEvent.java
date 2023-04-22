package com.jore.epoc.bo.accounting;

public class ProductionCostBookingEvent extends AbstractBookingEvent {
    @Override
    public void book(Accounting accounting) {
        int creditBooking = accounting.loss("3000", getAmount());
        int debitBooking = accounting.debit("1020", getAmount());
        accounting.journal(creditBooking, debitBooking, getBookingDate(), getBookingText());
        creditBooking = accounting.credit("1080", getAmount());
        debitBooking = accounting.debit("1072", getAmount());
        accounting.journal(creditBooking, debitBooking, getBookingDate(), "From Raw to product");
    }
}
