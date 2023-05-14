package com.jore.epoc.bo.accounting;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.jore.jpa.BusinessObject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Account extends BusinessObject {
    @ManyToOne(optional = false)
    private FinancialAccounting accounting;
    private String number;
    private BigDecimal startBalance = BigDecimal.ZERO;
    private String name;
    private AccountType accountType;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "creditAccount", orphanRemoval = true)
    private List<Booking> creditBookings = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "debitAccount", orphanRemoval = true)
    private List<Booking> debitBookings = new ArrayList<>();

    public Account() {
    }

    public Account(AccountType accountType, String number, String name) {
        this.accountType = accountType;
        this.number = number;
        this.name = name;
    }

    public void credit(Booking booking) {
        booking.setCreditAccount(this);
        creditBookings.add(booking);
    }

    public void debit(Booking booking) {
        booking.setDebitAccount(this);
        debitBookings.add(booking);
    }

    public BigDecimal getBalance() {
        BigDecimal result = startBalance;
        result = result.add(debitBookings.stream().map(booking -> booking.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add));
        result = result.add(creditBookings.stream().map(booking -> booking.getAmount().negate()).reduce(BigDecimal.ZERO, BigDecimal::add));
        return accountType.equals(AccountType.BALANCE_SHEET) ? result : result.negate();
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public BigDecimal getStartBalance() {
        return startBalance;
    }

    public void setAccounting(FinancialAccounting financialAccounting) {
        accounting = financialAccounting;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartBalance(BigDecimal startBalance) {
        this.startBalance = startBalance;
    }
}