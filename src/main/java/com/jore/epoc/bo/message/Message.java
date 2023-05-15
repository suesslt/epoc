package com.jore.epoc.bo.message;

import java.time.YearMonth;

import org.hibernate.annotations.Type;

import com.jore.epoc.bo.Company;
import com.jore.jpa.BusinessObject;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Message extends BusinessObject {
    @ManyToOne(optional = false)
    private Company company;
    @Type(com.jore.datatypes.hibernate.YearMonthUserType.class)
    private YearMonth relevantMonth;
    private String message;
    private MessageLevel level;

    public Company getCompany() {
        return company;
    }

    public MessageLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public YearMonth getRelevantMonth() {
        return relevantMonth;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setLevel(MessageLevel level) {
        this.level = level;
    }

    public void setMessage(String key, Object... param) {
        this.message = Messages.getMessage(key, param);
    }

    public void setRelevantMonth(YearMonth relevantMonth) {
        this.relevantMonth = relevantMonth;
    }

    @Override
    public String toString() {
        return "Message [company=" + company + ", relevantMonth=" + relevantMonth + ", message=" + message + ", level=" + level + "]";
    }
}
