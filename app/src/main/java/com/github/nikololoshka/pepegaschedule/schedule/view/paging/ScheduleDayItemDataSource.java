package com.github.nikololoshka.pepegaschedule.schedule.view.paging;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

import com.github.nikololoshka.pepegaschedule.BuildConfig;
import com.github.nikololoshka.pepegaschedule.utils.CommonUtils;

import java.util.Calendar;
import java.util.List;

import static com.github.nikololoshka.pepegaschedule.schedule.view.paging.ScheduleDayItemStorage.PAGE_SIZE;

/**
 * Загружает данные для адаптера с расписанием.
 */
public class ScheduleDayItemDataSource extends PageKeyedDataSource<Calendar, ScheduleDayItem> {

    private static final String TAG = "ScheduleDayItemDSLog";

    /**
     * Хранилище с расписанием.
     */
    @NonNull
    private ScheduleDayItemStorage mDayItemStorage;

    private ScheduleDayItemDataSource(@NonNull ScheduleDayItemStorage dayItemStorage) {
        mDayItemStorage = dayItemStorage;
        addInvalidatedCallback(new InvalidatedCallback() {
            @Override
            public void onInvalidated() {
                mDayItemStorage.reset();
            }
        });
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Calendar> params,
                            @NonNull LoadInitialCallback<Calendar, ScheduleDayItem> callback) {

        Calendar currentDay = mDayItemStorage.initialKey();

        // предыдущий день
        Calendar previousDay = (Calendar) currentDay.clone();
        previousDay.add(Calendar.DAY_OF_MONTH, -PAGE_SIZE);

        // следующий день
        Calendar nextDay = (Calendar) currentDay.clone();
        nextDay.add(Calendar.DAY_OF_MONTH, params.requestedLoadSize);

        // список с днями
        List<ScheduleDayItem> data = createData(currentDay, params.requestedLoadSize);

        callback.onResult(data, previousDay, nextDay);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Calendar> params,
                           @NonNull LoadCallback<Calendar, ScheduleDayItem> callback) {
        // предыдущий день
        Calendar adjacentDay = (Calendar) params.key.clone();
        adjacentDay.add(Calendar.DAY_OF_MONTH, -params.requestedLoadSize);

        // список с днями
        List<ScheduleDayItem> data = createData(params.key, params.requestedLoadSize);

        callback.onResult(data, adjacentDay);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Calendar> params,
                          @NonNull LoadCallback<Calendar, ScheduleDayItem> callback) {
        // следующий день
        Calendar adjacentDay = (Calendar) params.key.clone();
        adjacentDay.add(Calendar.DAY_OF_MONTH, params.requestedLoadSize);

        // список с днями
        List<ScheduleDayItem> data = createData(params.key, params.requestedLoadSize);

        callback.onResult(data, adjacentDay);
    }

    @NonNull
    private List<ScheduleDayItem> createData(@NonNull Calendar key, int requestedLoadSize) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, CommonUtils.dateToString(key, "dd.MM.yyy") + ": " + requestedLoadSize);
        }

        return mDayItemStorage.dayItems(key, requestedLoadSize);
    }

    /**
     * Фабрика для создания источника данных дней расписания.
     */
    public static class Factory extends DataSource.Factory<Calendar, ScheduleDayItem> {

        @NonNull
        private ScheduleDayItemStorage mDayItemStorage;

        public Factory(@NonNull ScheduleDayItemStorage dayItemStorage) {
            mDayItemStorage = dayItemStorage;
        }

        @NonNull
        @Override
        public DataSource<Calendar, ScheduleDayItem> create() {
            return new ScheduleDayItemDataSource(mDayItemStorage);
        }
    }
}
