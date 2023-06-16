package com.jore.epoc.bo.accounting;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.hibernate.annotations.Type;

import com.jore.Assert;
import com.jore.datatypes.currency.Currency;
import com.jore.datatypes.hibernate.CurrencyUserType;
import com.jore.datatypes.money.Money;
import com.jore.jpa.AbstractBusinessObject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Entity
public class FinancialAccounting extends AbstractBusinessObject {
    private static final int INFINITY_MULTIPLIER = 6;
    public static final String BANK = "1020";
    public static final String RECEIVABLES = "1100";
    public static final String RAW_MATERIALS = "1210";
    public static final String PRODUCTS = "1260";
    public static final String REAL_ESTATE = "1600";
    public static final String LONG_TERM_DEBT = "2100";
    public static final String PRODUCT_REVENUES = "3000";
    public static final String MATERIALAUFWAND = "4000";
    public static final String SERVICES = "4400";
    public static final String BESTANDESAENDERUNGEN_ROHWAREN = "4501";
    public static final String BESTANDESAENDERUNGEN_PRODUKTE = "4502";
    public static final String SALARIES = "5000";
    public static final String RAUMAUFWAND = "6000";
    public static final String DEPRECIATION = "6800";
    public static final String INTEREST = "6900";
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accounting", orphanRemoval = true)
    private List<Account> accounts = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accounting", orphanRemoval = true)
    private List<JournalEntry> journalEntries = new ArrayList<>();
    @Type(CurrencyUserType.class)
    private Currency baseCurrency = Currency.getInstance("CHF");

    public FinancialAccounting() {
        addAccount(new Account(AccountType.BALANCE_SHEET, BANK, "Bank"));
        addAccount(new Account(AccountType.BALANCE_SHEET, RECEIVABLES, "Receivables"));
        addAccount(new Account(AccountType.BALANCE_SHEET, LONG_TERM_DEBT, "Bankverbindlichkeiten"));
        addAccount(new Account(AccountType.BALANCE_SHEET, REAL_ESTATE, "Liegenschaften"));
        addAccount(new Account(AccountType.BALANCE_SHEET, RAW_MATERIALS, "Rohmaterialvorrat"));
        addAccount(new Account(AccountType.BALANCE_SHEET, PRODUCTS, "Fertigprodukte"));
        addAccount(new Account(AccountType.INCOME_STATEMENT, SERVICES, "Bezogene Dienstleistungen"));
        addAccount(new Account(AccountType.INCOME_STATEMENT, PRODUCT_REVENUES, "Produktionsertrag"));
        addAccount(new Account(AccountType.INCOME_STATEMENT, INTEREST, "Zinsaufwand"));
        addAccount(new Account(AccountType.INCOME_STATEMENT, RAUMAUFWAND, "Gebäudeunterhalt"));
        addAccount(new Account(AccountType.INCOME_STATEMENT, DEPRECIATION, "Abschreibung"));
        addAccount(new Account(AccountType.INCOME_STATEMENT, SALARIES, "Personalaufwand"));
        addAccount(new Account(AccountType.INCOME_STATEMENT, MATERIALAUFWAND, "Materialaufwand Produktion"));
        addAccount(new Account(AccountType.INCOME_STATEMENT, BESTANDESAENDERUNGEN_ROHWAREN, "Bestandesänderung Rohwaren"));
        addAccount(new Account(AccountType.INCOME_STATEMENT, BESTANDESAENDERUNGEN_PRODUKTE, "Bestandesänderung Fertigprodukte"));
    }

    public void addAccount(Account account) {
        account.setAccounting(this);
        accounts.add(account);
    }

    public void book(String bookingText, LocalDate bookingDate, LocalDate valueDate, DebitCreditAmount... creditDebitAmounts) {
        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setBookingText(Objects.requireNonNull(bookingText, "Bookig text must not be null."));
        journalEntry.setBookingDate(Objects.requireNonNull(bookingDate, "Bookig date must not be null."));
        journalEntry.setValueDate(Objects.requireNonNull(valueDate, "Value date must not be null."));
        Assert.isTrue("At least one debit to credit amount required.", creditDebitAmounts.length > 0);
        for (DebitCreditAmount debitCreditAmount : creditDebitAmounts) {
            validateAmount(debitCreditAmount.amount());
            validateAccount(debitCreditAmount.debitAccountNumber());
            validateAccount(debitCreditAmount.creditAccountNumber());
            Booking booking = new Booking();
            booking.setAmount(debitCreditAmount.amount().getAmount());
            getAccount(debitCreditAmount.debitAccountNumber()).get().debit(booking);
            getAccount(debitCreditAmount.creditAccountNumber()).get().credit(booking);
            journalEntry.addBooking(booking);
        }
        addJournalEntry(journalEntry);
        log.debug(journalEntry);
    }

    public boolean checkFunds(Money minimumRequiredAmount, LocalDate valueDate) {
        return getBankBalance(valueDate).compareTo(Objects.requireNonNull(minimumRequiredAmount, "Minimum required amount must not be null.")) >= 0;
    }

    public Money getBalanceForAccount(String accountNumber, LocalDate valueDate) {
        validateAccount(accountNumber);
        return Money.of(baseCurrency, getAccount(accountNumber).get().getBalance(valueDate));
    }

    public Money getBankBalance(LocalDate valueDate) {
        return getBalanceForAccount(BANK, valueDate);
    }

    public Money getCash(LocalDate valueDate) {
        return getBalanceForAccount(BANK, valueDate);
    }

    public Money getCompanyValue(LocalDate valueDate) {
        return Money.add(getOwnersCapital(valueDate), getPnL(valueDate).multiply(INFINITY_MULTIPLIER));
    }

    public Money getLongTermDebt(LocalDate valueDate) {
        return getBalanceForAccount(LONG_TERM_DEBT, valueDate);
    }

    public Money getOwnersCapital(LocalDate valueDate) {
        return Money.of(baseCurrency, accounts.stream().filter(account -> account.getType().equals(AccountType.BALANCE_SHEET)).map(account -> account.getBalance(valueDate)).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    public Money getPnL(LocalDate valueDate) {
        return Money.of(baseCurrency, accounts.stream().filter(account -> account.getType().equals(AccountType.INCOME_STATEMENT)).map(account -> account.getBalance(valueDate)).reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    public Money getProductBalance(LocalDate valueDate) {
        return getBalanceForAccount(PRODUCTS, valueDate);
    }

    public Money getRawMaterialBalance(LocalDate valueDate) {
        return getBalanceForAccount(RAW_MATERIALS, valueDate);
    }

    public Money getRealEstateBalance(LocalDate valueDate) {
        return getBalanceForAccount(REAL_ESTATE, valueDate);
    }

    public Money getReceivables(LocalDate valueDate) {
        return getBalanceForAccount(RECEIVABLES, valueDate);
    }

    public Money getRevenues(LocalDate valueDate) {
        return getBalanceForAccount(PRODUCT_REVENUES, valueDate);
    }

    public Money getSalaries(LocalDate valueDate) {
        return getBalanceForAccount(SALARIES, valueDate);
    }

    public Money getTotalAssets(LocalDate valueDate) {
        Money result = getTotalCurrentAssets(valueDate);
        result = result.add(getTotalFixedAssets(valueDate));
        return result;
    }

    public Money getTotalCurrentAssets(LocalDate valueDate) {
        Money result = getBankBalance(valueDate);
        result = result.add(getProductBalance(valueDate));
        result = result.add(getRawMaterialBalance(valueDate));
        return result;
    }

    public Money getTotalCurrentLiabilities(LocalDate valueDate) {
        return getLongTermDebt(valueDate);
    }

    public Money getTotalFixedAssets(LocalDate valueDate) {
        return getRealEstateBalance(valueDate);
    }

    public Money getTotalLiabilities(LocalDate valueDate) {
        return getTotalCurrentLiabilities(valueDate);
    }

    public Money getTotalLiabilitiesAndOwnersEquity(LocalDate valueDate) {
        Money result = getTotalLiabilities(valueDate);
        result = result.add(getOwnersCapital(valueDate));
        return result;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public void setStartBalanceForAccount(String accountNumber, Money balance) {
        getAccount(accountNumber).get().setStartBalance(balance.getAmount());
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        for (Account account : accounts) {
            result.append("\n");
            result.append(account.getNumber());
            result.append(": ");
            result.append(account.getStartBalance());
            result.append(" (");
            result.append(account.getName());
            result.append(")");
        }
        result.append("\n");
        return result.toString();
    }

    private void addJournalEntry(JournalEntry journalEntry) {
        journalEntry.setAccounting(this);
        journalEntries.add(journalEntry);
    }

    private Optional<Account> getAccount(String number) {
        return accounts.stream().filter(account -> account.getNumber().equals(number)).findFirst();
    }

    private void validateAccount(String accountNumber) {
        Assert.isTrue(String.format("Account for number '%s' not found.", accountNumber), getAccount(accountNumber).isPresent());
    }

    private void validateAmount(Money amount) {
        Assert.isTrue(String.format("Booked currency (%s) must be equal to base currency (%s).", amount.getCurrency(), baseCurrency), baseCurrency.equals(amount.getCurrency()));
    }
}
