package ru.kaelth4s.framework.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.ChronoUnit.DAYS;

public class LocalDateUtil {
    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private LocalDateUtil() {}

    public static LocalDate getNearestDay(String nearestDayData) {
        LocalDate now = LocalDate.now();

        if (nearestDayData.equals("29-02"))
            return getNearestLeapDay(nearestDayData, now);
        else
            return getNearestNotLeapDay(nearestDayData, now);
    }

    private static LocalDate getNearestNotLeapDay(String nearestDayData, LocalDate now) {
        LocalDate pastYearTargetDay = parseWithYear(nearestDayData, now.getYear() - 1);
        LocalDate currentYearTargetDay = parseWithYear(nearestDayData, now.getYear());
        LocalDate futureYearTargetDay = parseWithYear(nearestDayData, now.getYear() + 1);

        long daysToPastYear = Math.abs(DAYS.between(now, pastYearTargetDay));
        long daysToCurrentYear = Math.abs(DAYS.between(now, currentYearTargetDay));
        long daysToFutureYear = Math.abs(DAYS.between(now, futureYearTargetDay));

        return (daysToPastYear < daysToCurrentYear && daysToPastYear < daysToFutureYear) ? pastYearTargetDay :
                (daysToCurrentYear < daysToPastYear && daysToCurrentYear < daysToFutureYear) ? currentYearTargetDay :
                futureYearTargetDay;
    }

    private static LocalDate getNearestLeapDay(String nearestDayData, LocalDate now) {
        for (int i = now.getYear(); i < now.getYear() + 4; i++) {
            if (isLeapYear(i)) {
                if (i < 2) return parseWithYear(nearestDayData, now.getYear() + 1);
                else if (i == 3) return parseWithYear(nearestDayData, now.getYear() - 1);

                LocalDate futureLeapDay = parseWithYear(nearestDayData, now.getYear() + 2);
                LocalDate pastLeapDay = parseWithYear(nearestDayData, now.getYear() - 2);

                return Math.abs(DAYS.between(now, futureLeapDay)) < Math.abs(DAYS.between(now, pastLeapDay)) ? futureLeapDay : pastLeapDay;
            }
        }
        throw new IllegalArgumentException("Something went wrong");
    }

    private static boolean isLeapYear(int year) {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
    }

    private static LocalDate parseWithYear(String nearestDayData, int year) {
        return LocalDate.parse(
                String.format("%s-%s", nearestDayData, year),
                dateTimeFormatter
        );
    }
}