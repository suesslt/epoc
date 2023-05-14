package com.jore.epoc;

import com.jore.datatypes.money.Money;

import lombok.Data;

@Data
public class TestReportData {
    private String balanceSheetDate = "Today";
    private String companyName = "The Best Company";
    private String cashThis = Money.of("CHF", 15000).toString();
    private String cashPrev = Money.of("CHF", 15000).toString();
    private String recThis = Money.of("CHF", 15000).toString();
    private String recPrev = Money.of("CHF", 15000).toString();
    private String invThis = Money.of("CHF", 15000).toString();
    private String invPrev = Money.of("CHF", 15000).toString();
}
