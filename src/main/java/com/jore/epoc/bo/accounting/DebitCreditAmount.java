package com.jore.epoc.bo.accounting;

import com.jore.datatypes.money.Money;

public record DebitCreditAmount(String debitAccount, String creditAccount, Money amount) {
}
