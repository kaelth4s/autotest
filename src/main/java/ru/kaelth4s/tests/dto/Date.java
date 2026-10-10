package ru.kaelth4s.tests.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Date {
    private LocalDate date;
    private LocalDateTime dateTime;

    public Date(String data) {
        if (data.contains("AM") || data.contains("PM")) getDateWithTime(data);
        else getDateWithoutTime(data);
    }

    public LocalDate getDate() {
        return date;
    }
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    private void getDateWithoutTime(String data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        date = LocalDate.parse(data, formatter);
        dateTime = null;
    }

    private void getDateWithTime(String data) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("MMMM d, yyyy h:mm a")
                .withLocale(Locale.ENGLISH);
        dateTime = LocalDateTime.parse(data, formatter);
        date = null;
    }
}