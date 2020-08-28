package com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.paging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.model.schedule.Schedule;
import com.vereshchagin.nikolay.stankinschedule.model.schedule.pair.Pair;
import com.vereshchagin.nikolay.stankinschedule.repository.ScheduleRepository;
import com.vereshchagin.nikolay.stankinschedule.utils.StorageErrorData;

import org.joda.time.LocalDate;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
    private LocalDate mFirstDate;
    /**
     * Последняя дата в расписании.
     */
    @Nullable
    private LocalDate mLastDate;
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
    private LocalDate mInitialKey;
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
    List<ScheduleDayItem> dayItems(@NonNull LocalDate key, int requestedLoadSize) {
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

            if (key.isAfter(mLastDate) || key.isBefore(mFirstDate)) {
                return Collections.emptyList();
            }
        }

        List<ScheduleDayItem> dayItems = new ArrayList<>();

        LocalDate iterator = new LocalDate(key);
        for (int i = 0; i < requestedLoadSize; i++) {
            if (mIsLimit && iterator.isAfter(mLastDate)) {
                break;
            }

            TreeSet<Pair> pairs = new TreeSet<>(mSchedule.pairsByDate(iterator));
            dayItems.add(new ScheduleDayItem(pairs, new LocalDate(iterator)));
            iterator = iterator.plusDays(1);
        }

        return dayItems;
    }

    /**
     * Загружает расписание.
     */
    void loadSchedule() {
        try {
            File scheduleFile = new File(mSchedulePath);

            ScheduleRepository repository = new ScheduleRepository();
            mSchedule = repository.load(scheduleFile.getAbsolutePath());
            mFirstDate = mSchedule.startDate();
            mLastDate = mSchedule.endDate();
            mErrorData = null;

            return;

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
    public void setInitialKey(@Nullable LocalDate key) {
        mInitialKey = key;
    }

    @Nullable
    public LocalDate firstDate() {
        return mFirstDate;
    }

    @Nullable
    public LocalDate lastDate() {
        return mLastDate;
    }

    public boolean limit() {
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
    LocalDate initialKey() {
        return mInitialKey == null ? LocalDate.now() : mInitialKey;
    }
}
