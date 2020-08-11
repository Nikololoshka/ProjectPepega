package com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonArray;

import java.util.Calendar;

/**
 * Абстактный класс дат пары в расписании.
 */
public abstract class DateItem implements Comparable<DateItem>, Parcelable {

    static final String JSON_DATE = "date";
    static final String JSON_FREQUENCY = "frequency";

    /**
     * @return день недели даты.
     */
    @NonNull
    public abstract DayOfWeek dayOfWeek();

    /**
     * @return возвращает полную дату в формате: YYYY.MM.DD.
     */
    @NonNull
    public abstract String fullDate();

    /**
     * @return первый день даты.
     */
    @NonNull
    public abstract Calendar firstDay();

    /**
     * @return последний день даты.
     */
    @NonNull
    public abstract Calendar lastDay();

    /**
     * @return периодичность даты.
     */
    @NonNull
    public abstract FrequencyEnum frequency();

    /**
     * Определяет, пересекаются ли даты между собой.
     * @param dateItem сравниваемая дата.
     * @return true если пересекаются, иначе false.
     */
    public abstract boolean intersect(@NonNull DateItem dateItem);

    /**
     * Добавляет DateItem в json массив.
     * @param array json массив.
     */
    public abstract void toJson(@NonNull JsonArray array);

    @Override
    public abstract boolean equals(@Nullable Object o);
}
