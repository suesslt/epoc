package com.jore.epoc.bo.orders;

import org.hibernate.annotations.CompositeType;
import org.hibernate.annotations.Type;

import com.jore.datatypes.money.Money;
import com.jore.datatypes.percent.Percent;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.CreditEventDirection;
import com.jore.epoc.bo.Message;
import com.jore.epoc.bo.MessageLevel;
import com.jore.epoc.bo.accounting.Accounting;
import com.jore.epoc.bo.accounting.BookingRecord;
import com.jore.epoc.bo.accounting.DebitCreditAmount;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AdjustCreditLineOrder extends AbstractSimulationOrder {
    private CreditEventDirection direction;
    @AttributeOverride(name = "amount", column = @Column(name = "adjust_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "adjust_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money adjustAmount;
    @Type(com.jore.datatypes.hibernate.PercentUserType.class)
    private Percent interestRate;

    @Override
    public void apply(Company company) {
        BookingRecord bookingRecord = new BookingRecord(getExecutionMonth().atDay(FIRST_OF_MONTH), "Adjustment of credit line", new DebitCreditAmount(Accounting.BANK, Accounting.LONG_TERM_DEBT, adjustAmount));
        company.book(bookingRecord);
        Message message = new Message();
        message.setRelevantMonth(getExecutionMonth());
        message.setLevel(MessageLevel.INFORMATION);
        message.setMessage(String.format("%s credit line for %s.", direction, adjustAmount));
        company.addMessage(message);
    }

    @Override
    public int getSortOrder() {
        return 1;
    }
}
