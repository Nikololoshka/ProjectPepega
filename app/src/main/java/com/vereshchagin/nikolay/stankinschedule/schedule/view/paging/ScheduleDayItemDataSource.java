package com.vereshchagin.nikolay.stankinschedule.schedule.view.paging;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

import com.vereshchagin.nikolay.stankinschedule.BuildConfig;
import com.vereshchagin.nikolay.stankinschedule.utils.CommonUtils;

import java.util.Calendar;
import java.util.List;

import static com.vereshchagin.nikolay.stankinschedule.schedule.view.paging.ScheduleDayItemStorage.PAGE_SIZE;

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
        super();
        mDayItemStorage = dayItemStorage;
    }

    @Override
    public void invalidate() {
        mDayItemStorage.reset();

        super.invalidate();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Calendar> params,
                            @NonNull LoadInitialCallback<Calendar, ScheduleDayItem> callback) {
        mDayItemStorage.loadSchedule();

        Calendar currentDay = mDayItemStorage.initialKey();

        // предыдущий день
        Calendar previousDay = (Calendar) currentDay.clone();
        previousDay.add(Calendar.DAY_OF_MONTH, -PAGE_SIZE);

        // следующий день
        Calendar nextDay = (Calendar) currentDay.clone();
        nextDay.add(Calendar.DAY_OF_MONTH, params.requestedLoadSize);

        int loadSize = params.requestedLoadSize;

        if (mDayItemStorage.limit()) {
            Calendar firstDate = mDayItemStorage.firstDate();
            Calendar lastDate = mDayItemStorage.lastDate();

            if (firstDate != null && lastDate != null) {
                // после последнего дня в расписании
                if (currentDay.after(lastDate)) {
                    // текущий день
                    currentDay = (Calendar) lastDate.clone();
                    loadSize = 1;

                    // предыдущий день
                    if (firstDate.equals(lastDate)) {
                        previousDay = null;
                    } else {
                        previousDay = (Calendar) currentDay.clone();
                        previousDay.add(Calendar.DAY_OF_MONTH, -PAGE_SIZE);
                    }

                    // следующий день
                    nextDay = null;

                } else if (currentDay.before(firstDate)) {
                    // текущий день
                    currentDay = (Calendar) firstDate.clone();

                    // предыдущий день
                    previousDay = null;

                    // следующий день
                    if (firstDate.equals(lastDate)) {
                        nextDay = null;
                    } else  {
                        nextDay = (Calendar) currentDay.clone();
                        nextDay.add(Calendar.DAY_OF_MONTH, loadSize);
                    }
                }
            }
        }

        // список с днями
        List<ScheduleDayItem> data = createData(currentDay, loadSize);

        callback.onResult(data, previousDay, nextDay);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Calendar> params,
                           @NonNull LoadCallback<Calendar, ScheduleDayItem> callback) {

        // предыдущая дата
        Calendar adjacentDay = (Calendar) params.key.clone();
        adjacentDay.add(Calendar.DAY_OF_MONTH, -params.requestedLoadSize);

        Calendar key = params.key;
        int loadSize = params.requestedLoadSize;

        if (mDayItemStorage.limit()) {
            Calendar firstDate = mDayItemStorage.firstDate();

            if (firstDate != null && params.key.before(firstDate)) {
                key = firstDate;
                loadSize = params.requestedLoadSize - (int) CommonUtils.calendarDiff(firstDate, params.key);
                adjacentDay = null;
            }
        }

        // список с днями
        List<ScheduleDayItem> data = createData(key, loadSize);

        mDayItemStorage.isThreadReset();

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
            Log.d(TAG, String.format("From %s: size %d",
                    CommonUtils.dateToString(key, "dd.MM.yyy"),
                    requestedLoadSize));
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
