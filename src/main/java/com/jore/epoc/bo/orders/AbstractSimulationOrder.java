package com.jore.epoc.bo.orders;

import java.time.LocalDate;
import java.time.YearMonth;

import org.hibernate.annotations.Type;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.accounting.DebitCreditAmount;
import com.jore.epoc.bo.message.Message;
import com.jore.epoc.bo.message.MessageLevel;
import com.jore.jpa.BusinessObject;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Entity
// TODO Check if subclasses can be stored in one table
public abstract class AbstractSimulationOrder extends BusinessObject implements SimulationOrder {
    protected static final int FIRST_OF_MONTH = 1;
    @Type(com.jore.datatypes.hibernate.YearMonthUserType.class)
    private YearMonth executionMonth;
    private boolean isExecuted = false;
    @ManyToOne(optional = false)
    private Company company;

    @Override
    public abstract void execute();

    public abstract Money getAmount();

    public Company getCompany() {
        return company;
    }

    @Override
    public YearMonth getExecutionMonth() {
        return executionMonth;
    }

    public abstract int getSortOrder();

    public abstract String getType();

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
        log.debug(message);
    }

    protected void book(LocalDate bookingDate, String bookingText, String debitAccount, String creditAccount, Money bookingAmount) {
        company.getAccounting().book(bookingText, bookingDate, bookingDate, new DebitCreditAmount(debitAccount, creditAccount, bookingAmount));
    }

    protected void book(LocalDate bookingDate, String bookingText, String debitAccount, String creditAccount, Money bookingAmount, String debitAccount2, String creditAccount2, Money bookingAmount2) {
        company.getAccounting().book(bookingText, bookingDate, bookingDate, new DebitCreditAmount(debitAccount, creditAccount, bookingAmount), new DebitCreditAmount(debitAccount2, creditAccount2, bookingAmount2));
    }
}
