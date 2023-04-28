package com.jore.epoc.bo.accounting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.annotations.Type;

import com.jore.datatypes.currency.Currency;
import com.jore.datatypes.hibernate.CurrencyUserType;
import com.jore.datatypes.money.Money;
import com.jore.jpa.BusinessObject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Entity
public class FinancialAccounting extends BusinessObject {
    public static final String LONG_TERM_DEBT = "2450";
    public static final String BANK = "1020";
    public static final String IMMOBILIEN = "1600";
    public static final String ROHWAREN = "1210";
    public static final String SERVICES = "4400";
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accounting", orphanRemoval = true)
    private List<Account> accounts = new ArrayList<>();
    @Type(CurrencyUserType.class)
    private Currency baseCurrency = Currency.getInstance("CHF");

    public FinancialAccounting() {
        setBaseCurrency(baseCurrency);
    }

    public void addAccount(Account account) {
        account.setAccounting(this);
        accounts.add(account);
    }

    public void book(BookingRecord bookingRecord) {
        String debitAccountNumber = bookingRecord.amount().debitAccount();
        Account debitAccount = getAccount(debitAccountNumber);
        debitAccount.debit(bookingRecord.amount().amount());
        String creditAccountNumber = bookingRecord.amount().creditAccount();
        Account creditAccount = getAccount(creditAccountNumber);
        creditAccount.credit(bookingRecord.amount().amount());
    }

    public boolean checkFunds(Money costsToBeCharged) {
        return getBank().compareTo(costsToBeCharged) >= 0;
    }

    public Money getBalanceForAccount(String accountNumber) {
        return getAccount(accountNumber).getBalance();
    }

    public Money getBank() {
        return getAccount(BANK).getBalance();
    }

    public Money getLongTermDebt() {
        return getAccount(LONG_TERM_DEBT).getBalance();
    }

    public Money getPnL() {
        return null;
    }

    public void setBalanceForAccount(String accountNumber, Money balance) {
        getAccount(accountNumber).setBalance(balance);
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
        addAccount(createAccount(BANK));
        addAccount(createAccount(LONG_TERM_DEBT));
        addAccount(createAccount(IMMOBILIEN));
        addAccount(createAccount(ROHWAREN));
        addAccount(createAccount(SERVICES));
    }

    private Account createAccount(String number) {
        Account result = new Account();
        result.setNumber(number);
        result.setBalance(Money.of(baseCurrency, 0));
        return result;
    }

    private Account getAccount(String number) {
        Optional<Account> result = accounts.stream().filter(account -> account.getNumber().equals(number)).findFirst();
        if (result.isEmpty()) {
            result = Optional.of(new Account());
            log.warn("Created temporary account for number '" + number + "'."); // TODO for development only, remove afterwrds
        }
        return result.get();
    }
}
