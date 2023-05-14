package com.jore.epoc.bo.accounting;

import com.jore.datatypes.money.Money;

public record DebitCreditAmount(String debitAccountNumber, String creditAccountNumber, Money amount) {
}
