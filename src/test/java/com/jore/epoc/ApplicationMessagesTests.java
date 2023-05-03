package com.jore.epoc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.MessageFormat;
import java.time.YearMonth;
import java.util.ResourceBundle;

import org.junit.jupiter.api.Test;

import com.jore.datatypes.money.Money;
import com.jore.epoc.bo.message.Messages;

class ApplicationMessagesTests {
    @Test
    void testGetString() {
        ResourceBundle myRessources = ResourceBundle.getBundle("TestApplicationMessages");
        assertEquals("We have built a beautiful factory for you!", myRessources.getString("FactoryBuiltSimple"));
    }

    @Test
    void testGetStringWithParams() {
        ResourceBundle myRessources = ResourceBundle.getBundle("TestApplicationMessages");
        MessageFormat format = new MessageFormat("");
        format.applyPattern(myRessources.getString("FactoryBuiltWithParams"));
        Object[] arguments = { YearMonth.of(2023, 1), Money.of("CHF", 500).toString() };
        String formatted = format.format(arguments);
        assertEquals("We have built a beautiful factory for you in month '2023-01' for 500.00 CHF.", formatted);
    }

    @Test
    void testProperties() {
        Messages.load("TestApplicationMessages");
        assertEquals("We have built a beautiful factory for you in month '2023-01' for 500.00 CHF.", Messages.getMessage("FactoryBuiltWithParams", YearMonth.of(2023, 1), Money.of("CHF", 500).toString()));
    }
}
