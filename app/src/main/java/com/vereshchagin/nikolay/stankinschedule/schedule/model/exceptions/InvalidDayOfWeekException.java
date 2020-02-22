package com.vereshchagin.nikolay.stankinschedule.schedule.model.exceptions;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Исключение, генерирующийся когда у дня не правильный день недели.
 */
public class InvalidDayOfWeekException extends IllegalArgumentException {

    public InvalidDayOfWeekException(@NonNull String message) {
        super(message);
    }

    @Deprecated
    public Calendar date() {
        return new GregorianCalendar();
    }
}
