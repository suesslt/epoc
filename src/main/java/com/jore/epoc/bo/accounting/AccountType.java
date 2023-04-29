package com.jore.epoc.bo.accounting;

import com.jore.datatypes.money.Money;

public enum AccountType {
    BALANCE_SHEET(1), INCOME_STATEMENT(-1);

    private final int sign;

    AccountType(int sign) {
        this.sign = sign;
    }

    Money adjustSign(Money amount) {
        return amount != null ? amount.multiply(sign) : null;
    }
}
