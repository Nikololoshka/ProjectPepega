package com.github.nikololoshka.pepegaschedule.schedule.model.pair;

import androidx.annotation.NonNull;

import com.github.nikololoshka.pepegaschedule.schedule.model.exceptions.InvalidDayOfWeekException;
import com.github.nikololoshka.pepegaschedule.utils.CommonUtils;

import java.util.Calendar;

/**
 * Дни недели даты в расписании.
 */
public enum DayOfWeek {

    /**
     * Понедельник.
     */
    MONDAY,

    /**
     * Вторник.
     */
    TUESDAY,

    /**
     * Среда.
     */
    WEDNESDAY,

    /**
     * Четверг.
     */
    THURSDAY,

    /**
     * Пятница.
     */
    FRIDAY,

    /**
     * Суббота.
     */
    SATURDAY;

    /**
     * Возвращает день недели соотвествующей даты.
     * @param date дата.
     * @return день недели.
     * @throws InvalidDayOfWeekException если не удалось узнать день недели.
     */
    public static DayOfWeek of(@NonNull Calendar date) {
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

        throw new InvalidDayOfWeekException("Invalid day of week: "
                + CommonUtils.dateToString(date, "dd.MM.yyyy"));
    }
}
