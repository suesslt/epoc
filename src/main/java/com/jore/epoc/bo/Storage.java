package com.jore.epoc.bo;

import java.time.YearMonth;

import com.jore.jpa.BusinessObject;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Storage extends BusinessObject {
    @ManyToOne(optional = false)
    private Company company;
    private int capacity;
    private YearMonth storageStartMonth;
}
