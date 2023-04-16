package com.jore.epoc.bo.events;

import java.time.YearMonth;

import org.hibernate.annotations.CompositeType;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.Company;
import com.jore.epoc.bo.CompanySimulationStep;
import com.jore.jpa.BusinessObject;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
// TODO Check if subclasses can be stored in one table
public abstract class SimulationEvent extends BusinessObject {
    protected static final int FIRST_OF_MONTH = 1;
    @ManyToOne(optional = false)
    private CompanySimulationStep companySimulationStep;
    @AttributeOverride(name = "amount", column = @Column(name = "fixed_cost_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "fixed_cost_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money fixedCosts;
    @AttributeOverride(name = "amount", column = @Column(name = "variable_cost_amount"))
    @AttributeOverride(name = "currency", column = @Column(name = "variable_cost_currency"))
    @CompositeType(com.jore.datatypes.hibernate.MoneyCompositeUserType.class)
    private Money variableCosts;

    public abstract void apply(Company company);

    protected YearMonth getEventMonth() {
        return companySimulationStep.getSimulationStep().getSimulationMonth();
    }
}
