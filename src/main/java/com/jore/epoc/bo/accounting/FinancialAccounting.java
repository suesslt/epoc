package com.jore.epoc.bo.accounting;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private static final int INFINITY_MULTIPLIER = 6;
    public static final String BANK = "1020";
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

    public void addJournalEntry(JournalEntry journalEntry) {
        journalEntry.setAccounting(this);
        journalEntries.add(journalEntry);
    }

    public void book(String bookingText, LocalDate bookingDate, LocalDate valueDate, DebitCreditAmount... creditDebitAmounts) {
        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setBookingText(bookingText);
        journalEntry.setBookingDate(bookingDate);
        journalEntry.setValueDate(valueDate);
        for (DebitCreditAmount creditDebitAmount : creditDebitAmounts) {
            Booking booking = new Booking();
            booking.setAmount(creditDebitAmount.amount().getAmount()); // TODO Test case if not found
            getAccount(creditDebitAmount.debitAccountNumber()).get().debit(booking);
            getAccount(creditDebitAmount.creditAccountNumber()).get().credit(booking);
            journalEntry.addBooking(booking);
        }
        addJournalEntry(journalEntry);
        log.debug(journalEntry);
    }

    public boolean checkFunds(Money costsToBeCharged, LocalDate valueDate) {
        return getBankBalance(valueDate).compareTo(costsToBeCharged) >= 0;
    }

    public Money getBalanceForAccount(String accountNumber, LocalDate valueDate) {
        Money result = null;
        Optional<Account> account = getAccount(accountNumber);
        if (account.isPresent()) {
            result = Money.of(baseCurrency, account.get().getBalance());
        } else {
            result = Money.of(baseCurrency, BigDecimal.ZERO); // TODO Consider to return Optional<Money>
        }
        return result;
    }

    public Money getBankBalance(LocalDate valueDate) {
        return getBalanceForAccount(BANK, valueDate);
    }

    public Money getCompanyValue(LocalDate valueDate) {
        return Money.add(getOwnersCapital(valueDate), getPnL(valueDate) != null ? getPnL(valueDate).multiply(INFINITY_MULTIPLIER) : null);
    }

    public Money getLongTermDebt(LocalDate valueDate) {
        return getBalanceForAccount(LONG_TERM_DEBT, valueDate);
    }

    // TODO stream accounts, filter and add
    public Money getOwnersCapital(LocalDate valueDate) {
        Money result = null;
        result = Money.add(result, getBalanceForAccount(BANK, valueDate));
        result = Money.add(result, getBalanceForAccount(REAL_ESTATE, valueDate));
        result = Money.add(result, getBalanceForAccount(RAW_MATERIALS, valueDate));
        result = Money.add(result, getBalanceForAccount(PRODUCTS, valueDate));
        result = Money.add(result, getBalanceForAccount(LONG_TERM_DEBT, valueDate));
        return result;
    }

    // TODO stream accounts, filter and add
    public Money getPnL(LocalDate valueDate) {
        Money result = null;
        result = Money.add(result, getBalanceForAccount(PRODUCT_REVENUES, valueDate));
        result = Money.add(result, getBalanceForAccount(SERVICES, valueDate));
        result = Money.add(result, getBalanceForAccount(INTEREST, valueDate));
        result = Money.add(result, getBalanceForAccount(RAUMAUFWAND, valueDate));
        result = Money.add(result, getBalanceForAccount(DEPRECIATION, valueDate));
        result = Money.add(result, getBalanceForAccount(SALARIES, valueDate));
        result = Money.add(result, getBalanceForAccount(MATERIALAUFWAND, valueDate));
        result = Money.add(result, getBalanceForAccount(BESTANDESAENDERUNGEN_ROHWAREN, valueDate));
        result = Money.add(result, getBalanceForAccount(BESTANDESAENDERUNGEN_PRODUKTE, valueDate));
        return result;
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

    public Money getRevenues(LocalDate valueDate) {
        return getBalanceForAccount(PRODUCT_REVENUES, valueDate);
    }

    public Money getSalaries(LocalDate valueDate) {
        return getBalanceForAccount(SALARIES, valueDate);
    }

    @Deprecated
    public void setBalanceForAccount(String accountNumber, Money balance) {
        getAccount(accountNumber).get().setStartBalance(balance.getAmount());
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
            result.append(account.getStartBalance());
            result.append(" (");
            result.append(account.getName());
            result.append(")");
        }
        result.append("\n");
        return result.toString();
    }

    private Optional<Account> getAccount(String number) {
        return accounts.stream().filter(account -> account.getNumber().equals(number)).findFirst();
    }
}
