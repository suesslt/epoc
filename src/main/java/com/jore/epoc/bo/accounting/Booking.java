package com.jore.epoc.bo.accounting;

import java.math.BigDecimal;

import com.jore.jpa.BusinessObject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Booking extends BusinessObject {
    private BigDecimal amount;
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private JournalEntry journalEntry;
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private Account creditAccount;
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private Account debitAccount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCreditAccount(Account creditAccount) {
        this.creditAccount = creditAccount;
    }

    public void setDebitAccount(Account debitAccount) {
        this.debitAccount = debitAccount;
    }

    public void setJournalEntry(JournalEntry journalEntry) {
        this.journalEntry = journalEntry;
    }

    @Override
    public String toString() {
        return "Booking [amount=" + amount + ", creditAccount=" + (creditAccount != null ? creditAccount.getName() : null) + ", debitAccount=" + (debitAccount != null ? debitAccount.getName() : null) + "]";
    }
}
