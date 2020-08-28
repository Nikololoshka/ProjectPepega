package com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.paging;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

import com.vereshchagin.nikolay.stankinschedule.BuildConfig;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.List;

import static com.vereshchagin.nikolay.stankinschedule.ui.schedule.view.paging.ScheduleDayItemStorage.PAGE_SIZE;

/**
 * Загружает данные для адаптера с расписанием.
 */
public class ScheduleDayItemDataSource extends PageKeyedDataSource<LocalDate, ScheduleDayItem> {

    private static final String TAG = "ScheduleDayItemDSLog";

    /**
     * Хранилище с расписанием.
     */
    @NonNull
    private ScheduleDayItemStorage mDayItemStorage;

    private ScheduleDayItemDataSource(@NonNull ScheduleDayItemStorage dayItemStorage) {
        super();
        mDayItemStorage = dayItemStorage;
    }

    @Override
    public void invalidate() {
        mDayItemStorage.reset();

        super.invalidate();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<LocalDate> params,
                            @NonNull LoadInitialCallback<LocalDate, ScheduleDayItem> callback) {

        mDayItemStorage.loadSchedule();
        LocalDate currentDay = mDayItemStorage.initialKey();

        // предыдущий день
        LocalDate previousDay = currentDay.minusDays(PAGE_SIZE);

        // следующий день
        LocalDate nextDay = currentDay.plusDays(params.requestedLoadSize);

        int loadSize = params.requestedLoadSize;

        if (mDayItemStorage.limit()) {
            LocalDate firstDate = mDayItemStorage.firstDate();
            LocalDate lastDate = mDayItemStorage.lastDate();

            if (firstDate != null && lastDate != null) {
                // после последнего дня в расписании
                if (currentDay.isAfter(lastDate)) {
                    // текущий день
                    currentDay = new LocalDate(lastDate);
                    loadSize = 1;

                    // предыдущий день
                    if (firstDate.equals(lastDate)) {
                        previousDay = null;
                    } else {
                        previousDay = currentDay.minusDays(PAGE_SIZE);
                    }

                    // следующий день
                    nextDay = null;

                } else if (currentDay.isBefore(firstDate)) {
                    // текущий день
                    currentDay = new LocalDate(firstDate);

                    // предыдущий день
                    previousDay = null;

                    // следующий день
                    if (firstDate.equals(lastDate)) {
                        nextDay = null;
                    } else  {
                        nextDay = currentDay.plusDays(loadSize);
                    }
                }
            }
        }

        // список с днями
        List<ScheduleDayItem> data = createData(currentDay, loadSize);

        callback.onResult(data, previousDay, nextDay);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<LocalDate> params,
                           @NonNull LoadCallback<LocalDate, ScheduleDayItem> callback) {

        // предыдущая дата
        LocalDate adjacentDay = params.key.minusDays(params.requestedLoadSize);

        LocalDate key = params.key;
        int loadSize = params.requestedLoadSize;

        if (mDayItemStorage.limit()) {
            LocalDate firstDate = mDayItemStorage.firstDate();

            if (firstDate != null && params.key.isBefore(firstDate)) {
                key = firstDate;
                loadSize = params.requestedLoadSize + Days.daysBetween(firstDate, params.key).getDays();
                adjacentDay = null;
            }
        }

        // список с днями
        List<ScheduleDayItem> data = createData(key, loadSize);
        Log.d(TAG, "loadBefore: " + data.size());

        mDayItemStorage.isThreadReset();

        callback.onResult(data, adjacentDay);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<LocalDate> params,
                          @NonNull LoadCallback<LocalDate, ScheduleDayItem> callback) {
        // следующий день
        LocalDate adjacentDay = params.key.plusDays(params.requestedLoadSize);

        // список с днями
        List<ScheduleDayItem> data = createData(params.key, params.requestedLoadSize);
        Log.d(TAG, "loadAfter: " + data.size());
        callback.onResult(data, adjacentDay);
    }

    @NonNull
    private List<ScheduleDayItem> createData(@NonNull LocalDate key, int requestedLoadSize) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format("From %s: size %d",
                    key.toString("dd.MM.yyy"),
                    requestedLoadSize));
        }

        return mDayItemStorage.dayItems(key, requestedLoadSize);
    }

    /**
     * Фабрика для создания источника данных дней расписания.
     */
    public static class Factory extends DataSource.Factory<LocalDate, ScheduleDayItem> {

        @NonNull
        private ScheduleDayItemStorage mDayItemStorage;

        public Factory(@NonNull ScheduleDayItemStorage dayItemStorage) {
            mDayItemStorage = dayItemStorage;
        }

        @NonNull
        @Override
        public DataSource<LocalDate, ScheduleDayItem> create() {
            return new ScheduleDayItemDataSource(mDayItemStorage);
        }
    }
}
