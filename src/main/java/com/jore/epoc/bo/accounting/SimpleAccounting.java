package com.jore.epoc.bo.accounting;

import java.util.HashMap;
import java.util.Map;

import com.jore.datatypes.currency.Currency;
import com.jore.datatypes.money.Money;

import lombok.Data;

public class SimpleAccounting implements Accounting {
    @Data
    public class AccountStub {
        private String number;
        private Money balance;

        public AccountStub(Money initialBalance) {
            balance = initialBalance;
        }

        public void credit(Money amount) {
            balance = Money.subtract(balance, amount);
        }

        public void debit(Money amount) {
            balance = Money.add(balance, amount);
        }
    }

    private Map<String, AccountStub> accounts = new HashMap<>();
    private Currency baseCurrency = Currency.getInstance("CHF");

    public SimpleAccounting() {
        setBaseCurrency(baseCurrency);
    }

    @Override
    public void book(BookingRecord bookingRecord) {
        String debitAccountNumber = bookingRecord.amount().debitAccount();
        AccountStub debitAccount = accounts.get(debitAccountNumber);
        debitAccount.debit(bookingRecord.amount().amount());
        String creditAccountNumber = bookingRecord.amount().creditAccount();
        AccountStub creditAccount = accounts.get(creditAccountNumber);
        creditAccount.credit(bookingRecord.amount().amount());
    }

    @Override
    public boolean checkFunds(Money costsToBeCharged) {
        return getBank().compareTo(costsToBeCharged) > 0;
    }

    public Money getBalanceForAccount(String accountNumber) {
        return accounts.get(accountNumber).getBalance();
    }

    @Override
    public Money getBank() {
        return accounts.get(BANK).getBalance();
    }

    @Override
    public Money getLongTermDebt() {
        return accounts.get(LONG_TERM_DEBT).getBalance();
    }

    @Override
    public Money getPnL() {
        return null;
    }

    public void setBalanceForAccount(String accountNumber, Money balance) {
        accounts.get(accountNumber).setBalance(balance);
    }

    @Override
    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
        accounts.put(BANK, new AccountStub(Money.of(baseCurrency, 0)));
        accounts.put(LONG_TERM_DEBT, new AccountStub(Money.of(baseCurrency, 0)));
        accounts.put(IMMOBILIEN, new AccountStub(Money.of(baseCurrency, 0)));
        accounts.put(ROHWAREN, new AccountStub(Money.of(baseCurrency, 0)));
    }
}
