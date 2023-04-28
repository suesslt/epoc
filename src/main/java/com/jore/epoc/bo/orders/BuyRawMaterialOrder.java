package com.jore.epoc.bo.orders;

import org.hibernate.annotations.CompositeType;

import com.jore.Assert;
import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.MessageLevel;
import com.jore.epoc.bo.Storage;
import com.jore.epoc.bo.accounting.FinancialAccounting;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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
        Money cost = unitPrice.multiply(amount);
        if (storageCapacity >= amount && getCompany().checkFunds(cost)) {
            Storage.distributeRawMaterialAccrossStorages(getCompany().getStorages(), amount, getExecutionMonth());
            book(getExecutionMonth().atDay(FIRST_OF_MONTH), "Built storage", FinancialAccounting.ROHWAREN, FinancialAccounting.BANK, cost);
            addMessage("Bought raw materials.", MessageLevel.INFORMATION);
            setExecuted(true);
        } else {
            if (storageCapacity < amount) {
                addMessage("Could not buy raw material due to missing storage capacity.", MessageLevel.WARNING);
            } else {
                addMessage("Could not buy raw material due to insufficent funds.", MessageLevel.WARNING);
            }
        }
    }

    @Override
    public int getSortOrder() {
        return 3;
    }
}
