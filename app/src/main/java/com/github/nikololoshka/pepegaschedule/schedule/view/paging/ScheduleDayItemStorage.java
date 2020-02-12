package com.github.nikololoshka.pepegaschedule.schedule.view.paging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.schedule.model.Schedule;
import com.github.nikololoshka.pepegaschedule.schedule.model.pair.Pair;
import com.github.nikololoshka.pepegaschedule.utils.CommonUtils;
import com.github.nikololoshka.pepegaschedule.utils.StorageErrorData;
import com.google.gson.JsonParseException;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TreeSet;

/**
 * Хранилище с расписанием.
 */
public class ScheduleDayItemStorage {

    public static final int PAGE_SIZE = 20;

    public interface OnStorageListener {
        void onError();
    }

    /**
     * Расписание.
     */
    @Nullable
    private Schedule mSchedule;
    /**
     * Первая дата в расписании.
     */
    @Nullable
    private Calendar mFirstDate;
    /**
     * Последняя дата в расписании.
     */
    @Nullable
    private Calendar mLastDate;
    /**
     * Ограничивать ли расписание.
     */
    private boolean mIsLimit;
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
    /**
     * Ошибка при работе хранилища.
     */
    @Nullable
    private StorageErrorData mErrorData;
    /**
     * Listener для слежки изменения расписания.
     */
    @NonNull
    private OnStorageListener mListener;
    /**
     * Было ли перезагруженно расписание.
     */
    private boolean mIsReset = false;


    public ScheduleDayItemStorage(@NonNull String schedulePath, @NonNull OnStorageListener listener) {
        mSchedulePath = schedulePath;
        mListener = listener;
        mIsLimit = false;
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

        if (mSchedule == null) {
            return Collections.emptyList();
        }

        if (mIsLimit) {
            if (mFirstDate == null || mLastDate == null) {
                return Collections.emptyList();
            }

            if (key.after(mLastDate) || key.before(mFirstDate)) {
                return Collections.emptyList();
            }
        }

        List<ScheduleDayItem> dayItems = new ArrayList<>();

        Calendar iterator = (Calendar) key.clone();
        for (int i = 0; i < requestedLoadSize; i++) {
            if (mIsLimit && iterator.after(mLastDate)) {
                break;
            }

            TreeSet<Pair> pairs = mSchedule.pairsByDate(iterator);
            dayItems.add(new ScheduleDayItem(pairs, (Calendar) iterator.clone()));
            iterator.add(Calendar.DAY_OF_MONTH, 1);
        }

        return dayItems;
    }

    /**
     * Загружает расписание.
     */
    void loadSchedule() {
        try {
            File scheduleFile = new File(mSchedulePath);
            String json = FileUtils.readFileToString(scheduleFile, StandardCharsets.UTF_8);

            mSchedule = Schedule.fromJson(json);
            mFirstDate = mSchedule.firstDate();
            mLastDate = mSchedule.lastDate();
            mErrorData = null;

            return;

        } catch (JsonParseException e) {
            mErrorData = new StorageErrorData(R.string.sch_view_error_loading,
                    e.getLocalizedMessage());

        } catch (IOException e) {
            mErrorData = new StorageErrorData(R.string.sch_view_error_loading,
                    e.getLocalizedMessage());

        } catch (Exception e) {
            mErrorData = new StorageErrorData(R.string.sch_view_error_loading,
                    e.getLocalizedMessage());
        }

        mListener.onError();
    }

    /**
     * @return ошибка при работе.
     */
    @Nullable
    public StorageErrorData errorData() {
        return mErrorData;
    }

    /**
     * Сбрасывает загруженное расписание.
     */
    void reset() {
        mSchedule = null;
        mFirstDate = null;
        mLastDate = null;
        mIsReset = true;
    }

    void isThreadReset() {
        if (mIsReset) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException ignored) {

            }
            mIsReset = false;
        }
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

    @Nullable
    Calendar firstDate() {
        return mFirstDate;
    }

    @Nullable
    Calendar lastDate() {
        return mLastDate;
    }

    boolean limit() {
        return mIsLimit;
    }

    /**
     * Устанавливает, нужно ли ограничивать расписание.
     * @param limit true нужно ограничивать, иначе false.
     * @return изменилось ли значение.
     */
    public boolean setLimit(boolean limit) {
        if (limit == mIsLimit) {
            return false;
        }
        mIsLimit = limit;
        return true;
    }

    /**
     * @return инициализирующая дата.
     */
    @NonNull
    Calendar initialKey() {
        return mInitialKey == null ? CommonUtils.normalizeDate(new GregorianCalendar()) : mInitialKey;
    }
}
