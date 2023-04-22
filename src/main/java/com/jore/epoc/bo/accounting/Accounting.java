package com.jore.epoc.bo.accounting;

import java.time.LocalDate;

import com.jore.datatypes.money.Money;

public interface Accounting {
    int credit(String rubrik, Money amount);

    int debit(String rubrik, Money amount);

    Money getPnL();

    void journal(int creditBooking, int debitBooking, LocalDate bookingDate, String bookingText);

    int loss(String rubrik, Money amount);

    int profit(String rubrik, Money amount);
}
