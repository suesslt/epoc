package com.jore.epoc.bo.accounting;

import java.util.ArrayList;
import java.util.List;

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
    private static final int INFINITY_MULTIPLIER = 6;
    public static final String LONG_TERM_DEBT = "2100";
    public static final String BANK = "1020";
    public static final String REAL_ESTATE = "1600";
    public static final String ROHWAREN = "1210";
    public static final String SERVICES = "4400";
    public static final String PRODUKTE_ERLOESE = "3000";
    public static final String INTEREST = "6900";
    public static final String RAUMAUFWAND = "6000";
    public static final String DEPRECIATION = "6800";
    public static final String SALARIES = "5000";
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accounting", orphanRemoval = true)
    private List<Account> accounts = new ArrayList<>();
    @Type(CurrencyUserType.class)
    private Currency baseCurrency = Currency.getInstance("CHF");

    public FinancialAccounting() {
        addAccount(new Account(AccountType.BALANCE_SHEET, BANK, "Bank"));
        addAccount(new Account(AccountType.BALANCE_SHEET, LONG_TERM_DEBT, "Bankverbindlichkeiten"));
        addAccount(new Account(AccountType.BALANCE_SHEET, REAL_ESTATE, "Liegenschaften"));
        addAccount(new Account(AccountType.BALANCE_SHEET, ROHWAREN, "Rohmaterialvorrat"));
        addAccount(new Account(AccountType.INCOME_STATEMENT, SERVICES, "Bezogene Dienstleistungen"));
        addAccount(new Account(AccountType.INCOME_STATEMENT, PRODUKTE_ERLOESE, "Produktionsertrag"));
        addAccount(new Account(AccountType.INCOME_STATEMENT, INTEREST, "Zinsaufwand"));
        addAccount(new Account(AccountType.INCOME_STATEMENT, RAUMAUFWAND, "Gebäudeunterhalt"));
        addAccount(new Account(AccountType.INCOME_STATEMENT, DEPRECIATION, "Abschreibung"));
        addAccount(new Account(AccountType.INCOME_STATEMENT, SALARIES, "Abschreibung"));
    }

    public void addAccount(Account account) {
        account.setAccounting(this);
        accounts.add(account);
    }

    public void book(BookingRecord bookingRecord) {
        log.debug(bookingRecord);
        String debitAccountNumber = bookingRecord.amount().debitAccount();
        Account debitAccount = getAccount(debitAccountNumber);
        debitAccount.debit(bookingRecord.amount().amount());
        String creditAccountNumber = bookingRecord.amount().creditAccount();
        Account creditAccount = getAccount(creditAccountNumber);
        creditAccount.credit(bookingRecord.amount().amount());
    }

    public boolean checkFunds(Money costsToBeCharged) {
        return getBankBalance().compareTo(costsToBeCharged) >= 0;
    }

    public Money getBalanceForAccount(String accountNumber) {
        return nullToZero(getAccount(accountNumber).getBalance());
    }

    public Money getBankBalance() {
        return nullToZero(getAccount(BANK).getBalance());
    }

    public Money getCompanyValue() {
        return Money.add(getOwnersCapital(), getPnL() != null ? getPnL().multiply(INFINITY_MULTIPLIER) : null);
    }

    public Money getLongTermDebt() {
        return nullToZero(getAccount(LONG_TERM_DEBT).getBalance()).negate();
    }

    public Money getOwnersCapital() {
        Money result = null;
        result = Money.add(result, getBalanceForAccount(BANK));
        result = Money.add(result, getBalanceForAccount(REAL_ESTATE));
        result = Money.add(result, getBalanceForAccount(ROHWAREN));
        result = Money.add(result, getBalanceForAccount(LONG_TERM_DEBT));
        return result;
    }

    public Money getPnL() {
        Money result = null;
        result = Money.add(result, getBalanceForAccount(PRODUKTE_ERLOESE));
        result = Money.add(result, getBalanceForAccount(SERVICES));
        result = Money.add(result, getBalanceForAccount(INTEREST));
        result = Money.add(result, getBalanceForAccount(RAUMAUFWAND));
        result = Money.add(result, getBalanceForAccount(DEPRECIATION));
        result = Money.add(result, getBalanceForAccount(SALARIES));
        return result;
    }

    public Money getRealEstateBalance() {
        return nullToZero(getAccount(REAL_ESTATE).getBalance());
    }

    public void setBalanceForAccount(String accountNumber, Money balance) {
        getAccount(accountNumber).setBalance(balance);
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        for (Account account : accounts) {
            result.append("\n");
            result.append(account.getNumber());
            result.append(": ");
            result.append(account.getBalance());
            result.append(" (");
            result.append(account.getName());
            result.append(")");
        }
        result.append("\n");
        result.append("PnL:           ");
        result.append(getPnL());
        result.append("\n");
        result.append("Company Value: ");
        result.append(getCompanyValue());
        return result.toString();
    }

    private Account getAccount(String number) {
        return accounts.stream().filter(account -> account.getNumber().equals(number)).findFirst().get();
    }

    private Money nullToZero(Money balance) {
        return balance == null ? Money.of(baseCurrency, 0) : balance;
    }
}
