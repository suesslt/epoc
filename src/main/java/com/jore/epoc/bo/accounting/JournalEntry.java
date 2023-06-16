package com.jore.epoc.bo.accounting;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.jore.jpa.AbstractBusinessObject;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class JournalEntry extends AbstractBusinessObject {
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private FinancialAccounting accounting;
    private String bookingText;
    private LocalDate bookingDate;
    private LocalDate valueDate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "journalEntry", orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    public void addBooking(Booking booking) {
        booking.setJournalEntry(this);
        bookings.add(booking);
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public void setAccounting(FinancialAccounting accounting) {
        this.accounting = accounting;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public void setBookingText(String bookingText) {
        this.bookingText = bookingText;
    }

    public void setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
    }

    @Override
    public String toString() {
        return "JournalEntry [bookingText=" + bookingText + ", bookingDate=" + bookingDate + ", valueDate=" + valueDate + ", bookings=" + bookings.stream().map(booking -> booking.toString()).collect(Collectors.joining(",")) + "]";
    }
}
