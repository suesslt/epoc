package com.jore.epoc.bo.accounting;

import java.time.LocalDate;

import com.jore.datatypes.money.Money;

public class MilchbuechliAccounting implements Accounting {
    private Money credit;
    private Money debit;
    private Money profit;
    private Money loss;

    @Override
    public int credit(String rubrik, Money amount) {
        credit = Money.add(credit, amount);
        return 0;
    }

    @Override
    public int debit(String rubrik, Money amount) {
        debit = Money.add(debit, amount);
        return 0;
    }

    @Override
    public Money getPnL() {
        return Money.add(Money.subtract(credit, debit), Money.subtract(profit, loss));
    }

    @Override
    public void journal(int creditBooking, int debitBooking, LocalDate bookingDate, String bookingText) {
        // TODO Auto-generated method stub
    }

    @Override
    public int loss(String rubrik, Money amount) {
        loss = Money.add(loss, amount);
        return 0;
    }

    @Override
    public int profit(String rubrik, Money amount) {
        profit = Money.add(profit, amount);
        return 0;
    }
}
