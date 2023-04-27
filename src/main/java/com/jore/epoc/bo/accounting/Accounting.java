package com.jore.epoc.bo.accounting;

import com.jore.datatypes.currency.Currency;
import com.jore.datatypes.money.Money;

public interface Accounting {
    public static final String LONG_TERM_DEBT = "2450";
    public static final String BANK = "1020";

    void book(BookingRecord bookingRecord);

    boolean checkFunds(Money costsToBeCharged);

    Money getBank();

    Money getLongTermDebt();

    Money getPnL();

    void setBaseCurrency(Currency baseCurrency);
}
