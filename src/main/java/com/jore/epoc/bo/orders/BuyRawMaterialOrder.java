package com.jore.epoc.bo.orders;

import org.hibernate.annotations.CompositeType;

import com.jore.Assert;
import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Storage;
import com.jore.epoc.bo.accounting.FinancialAccounting;
import com.jore.epoc.bo.message.MessageLevel;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class BuyRawMaterialOrder extends AbstractSimulationOrder {
    private Integer amount;
    @AttributeOverride(name = "amount", column = @Column(name = "unit_price_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "unit_price_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money unitPrice;

    @Override
    public void execute() {
        Assert.notNull("Amount must not be null", amount);
        Assert.notNull("Unit price must not be null", unitPrice);
        int storageCapacity = getCompany().getStorages().stream().mapToInt(storage -> storage.getAvailableCapacity(getExecutionMonth())).sum();
        if (storageCapacity >= amount && getCompany().getAccounting().checkFunds(getAmount(), getExecutionMonth().atEndOfMonth())) {
            Storage.distributeRawMaterialAccrossStorages(getCompany().getStorages(), amount, getExecutionMonth(), unitPrice);
            book(getExecutionMonth().atDay(FIRST_OF_MONTH), "Buy of raw material", FinancialAccounting.MATERIALAUFWAND, FinancialAccounting.BANK, getAmount(), FinancialAccounting.RAW_MATERIALS, FinancialAccounting.BESTANDESAENDERUNGEN_ROHWAREN, getAmount());
            addMessage(MessageLevel.INFORMATION, "RawMaterialBought", amount, getExecutionMonth());
            setExecuted(true);
        } else {
            if (!getCompany().getAccounting().checkFunds(getAmount(), getExecutionMonth().atEndOfMonth())) {
                addMessage(MessageLevel.WARNING, "NoRawMaterialFunds", getExecutionMonth(), getAmount(), getCompany().getAccounting().getBankBalance(getExecutionMonth().atEndOfMonth()));
            } else {
                addMessage(MessageLevel.WARNING, "NoRawMaterialCapacity", getExecutionMonth(), amount, storageCapacity);
            }
        }
    }

    @Override
    public Money getAmount() {
        return unitPrice.multiply(amount);
    }

    @Override
    public int getSortOrder() {
        return 3;
    }

    @Override
    public String getType() {
        return "Buy raw material";
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setUnitPrice(Money unitPrice) {
        this.unitPrice = unitPrice;
    }
}
