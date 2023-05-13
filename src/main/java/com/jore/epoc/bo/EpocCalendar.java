package com.jore.epoc.bo;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class EpocCalendar {
    private static EpocCalendar INSTANCE = new EpocCalendar();

    public static EpocCalendar getInstance() {
        return INSTANCE;
    }

    public boolean isWorkingDay(LocalDate date) {
        return !date.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !date.getDayOfWeek().equals(DayOfWeek.SUNDAY);
    }
}
