package com.jore.epoc.bo;

import java.time.YearMonth;

import com.jore.jpa.BusinessObject;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Message extends BusinessObject {
    @ManyToOne(optional = false)
    private Company company;
    private YearMonth relevantMonth;
    private String message;
    private MessageLevel level;
}
