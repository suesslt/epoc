package com.jore.epoc.bo.orders;

import java.time.LocalDate;
import java.time.YearMonth;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.accounting.BookingRecord;
import com.jore.epoc.bo.accounting.DebitCreditAmount;
import com.jore.epoc.bo.message.Message;
import com.jore.epoc.bo.message.MessageLevel;
import com.jore.jpa.BusinessObject;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
// TODO Check if subclasses can be stored in one table
public abstract class AbstractSimulationOrder extends BusinessObject implements SimulationOrder {
    protected static final int FIRST_OF_MONTH = 1;
    private YearMonth executionMonth;
    private boolean isExecuted = false;
    @ManyToOne(optional = false)
    private Company company;

    @Override
    public abstract void execute();

    public Company getCompany() {
        return company;
    }

    @Override
    public YearMonth getExecutionMonth() {
        return executionMonth;
    }

    public abstract int getSortOrder();

    @Override
    public boolean isExecuted() {
        return isExecuted;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setExecuted(boolean isExecuted) {
        this.isExecuted = isExecuted;
    }

    public void setExecutionMonth(YearMonth executionMonth) {
        this.executionMonth = executionMonth;
    }

    protected void addMessage(MessageLevel level, String key, Object... parms) {
        Message message = new Message();
        message.setRelevantMonth(getExecutionMonth());
        message.setLevel(level);
        message.setMessage(key, parms);
        company.addMessage(message);
    }

    protected void book(LocalDate bookingDate, String bookingText, String debitAccount, String creditAccount, Money bookingAmount) {
        company.getAccounting().book(new BookingRecord(bookingDate, bookingText, new DebitCreditAmount(debitAccount, creditAccount, bookingAmount)));
    }

    protected void book(LocalDate bookingDate, String bookingText, String debitAccount, String creditAccount, Money bookingAmount, String debitAccount2, String creditAccount2, Money bookingAmount2) {
        company.getAccounting().book(new BookingRecord(bookingDate, bookingText, new DebitCreditAmount(debitAccount, creditAccount, bookingAmount)));
        company.getAccounting().book(new BookingRecord(bookingDate, bookingText, new DebitCreditAmount(debitAccount2, creditAccount2, bookingAmount2)));
    }
}
