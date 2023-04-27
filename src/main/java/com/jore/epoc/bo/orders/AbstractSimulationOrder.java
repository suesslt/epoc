package com.jore.epoc.bo.orders;

import java.time.LocalDate;
import java.time.YearMonth;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.Message;
import com.jore.epoc.bo.MessageLevel;
import com.jore.epoc.bo.accounting.BookingRecord;
import com.jore.epoc.bo.accounting.DebitCreditAmount;
import com.jore.jpa.BusinessObject;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
// TODO Check if subclasses can be stored in one table
public abstract class AbstractSimulationOrder extends BusinessObject implements SimulationOrder {
    protected static final int FIRST_OF_MONTH = 1;
    private YearMonth executionMonth;
    private boolean isExecuted = false;
    @ManyToOne(optional = false)
    private Company company;

    @Override
    public abstract void execute();

    public abstract int getSortOrder();

    protected void addMessage(String messageText, MessageLevel level) {
        Message message = new Message();
        message.setRelevantMonth(getExecutionMonth());
        message.setLevel(level);
        message.setMessage(messageText);
        company.addMessage(message);
    }

    protected void book(LocalDate bookingDate, String bookingText, String debitAccount, String creditAccount, Money bookingAmount) {
        company.book(new BookingRecord(bookingDate, bookingText, new DebitCreditAmount(debitAccount, creditAccount, bookingAmount)));
    }
}
