package com.github.nikololoshka.pepegaschedule.schedule.model.pair.exceptions;

import java.util.Calendar;

public class InvalidDayOfWeekException extends IllegalArgumentException {

    final private Calendar mDate;

    public InvalidDayOfWeekException(Calendar date) {
        super("Not valid day of the week: " + date);
        mDate = date;
    }

    public Calendar date() {
        return mDate;
    }
}
