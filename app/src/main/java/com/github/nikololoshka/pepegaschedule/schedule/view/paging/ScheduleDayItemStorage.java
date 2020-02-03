package com.github.nikololoshka.pepegaschedule.schedule.view.paging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.nikololoshka.pepegaschedule.schedule.model.Schedule;
import com.github.nikololoshka.pepegaschedule.schedule.model.pair.Pair;
import com.github.nikololoshka.pepegaschedule.utils.CommonUtils;
import com.google.gson.JsonParseException;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TreeSet;

/**
 * Хранилище с расписанием.
 */
public class ScheduleDayItemStorage {

    public static final int PAGE_SIZE = 20;

    /**
     * Расписание.
     */
    @Nullable
    private Schedule mSchedule;
    /**
     * Путь к расписанию.
     */
    @NonNull
    private String mSchedulePath;
    /**
     * Иннициализирующий ключ.
     */
    @Nullable
    private Calendar mInitialKey;

    public ScheduleDayItemStorage(@NonNull String schedulePath) {
        mSchedulePath = schedulePath;
    }

    /**
     * Возвращает дни с парами.
     * @param key начальная дата.
     * @param requestedLoadSize количесво дней.
     * @return список с днями с парами.
     */
    @NonNull
    List<ScheduleDayItem> dayItems(@NonNull Calendar key, int requestedLoadSize) {
        if (mSchedule == null) {
            loadSchedule();
        }

        List<ScheduleDayItem> dayItems = new ArrayList<>();

        Calendar iterator = (Calendar) key.clone();
        for (int i = 0; i < requestedLoadSize; i++) {
            TreeSet<Pair> pairs = mSchedule.pairsByDate(iterator);
            dayItems.add(new ScheduleDayItem(pairs, (Calendar) iterator.clone()));
            iterator.add(Calendar.DAY_OF_MONTH, 1);
        }

        return dayItems;
    }

    /**
     * Загружает расписание.
     */
    private void loadSchedule() {
        try {
            File scheduleFile = new File(mSchedulePath);
            String json = FileUtils.readFileToString(scheduleFile, StandardCharsets.UTF_8);
            mSchedule = Schedule.fromJson(json);

        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Сбрасывает загруженное расписание.
     */
    public void reset() {
        mSchedule = null;
    }

    /**
     * Устанавливает путь к расписанию.
     * @param schedulePath путь к расписанию.
     */
    public void setSchedulePath(@NonNull String schedulePath) {
        mSchedulePath = schedulePath;
        reset();
    }

    /**
     * Устанавливает инициализирующую дату.
     * @param key инициализирующая дата.
     */
    public void setInitialKey(@Nullable Calendar key) {
        mInitialKey = key;
    }

    /**
     * @return инициализирующая дата.
     */
    @NonNull
    public Calendar initialKey() {
        return mInitialKey == null ? CommonUtils.normalizeDate(new GregorianCalendar()) : mInitialKey;
    }
}
