package com.github.nikololoshka.pepegaschedule.schedule.pair;

import androidx.annotation.Nullable;

import com.github.nikololoshka.pepegaschedule.schedule.pair.exceptions.InvalidDayOfWeekException;

import java.util.Calendar;

public enum DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY;

    static public DayOfWeek valueOf(@Nullable Calendar date) {
        if (date == null) {
            throw new IllegalArgumentException("Day of week is null");
        }

        int dayNumber = date.get(Calendar.DAY_OF_WEEK);
        switch (dayNumber) {
            case Calendar.MONDAY:
                return DayOfWeek.MONDAY;
            case Calendar.TUESDAY:
                return DayOfWeek.TUESDAY;
            case Calendar.WEDNESDAY:
                return DayOfWeek.WEDNESDAY;
            case Calendar.THURSDAY:
                return DayOfWeek.THURSDAY;
            case Calendar.FRIDAY:
                return DayOfWeek.FRIDAY;
            case Calendar.SATURDAY:
                return DayOfWeek.SATURDAY;
        }

        throw new InvalidDayOfWeekException(date);
    }
}
