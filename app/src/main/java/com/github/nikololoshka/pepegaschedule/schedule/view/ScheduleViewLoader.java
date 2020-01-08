package com.github.nikololoshka.pepegaschedule.schedule.view;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nikololoshka.pepegaschedule.schedule.Schedule;
import com.github.nikololoshka.pepegaschedule.schedule.pair.Pair;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TreeSet;


/**
 * Загрузчик расписания.
 */
public class ScheduleViewLoader extends AsyncTaskLoader<ScheduleViewLoader.ScheduleDataView> {

    static final int REQUEST_SAVE_SCHEDULE = 1;
    static final int REQUEST_LOAD_SCHEDULE = 2;

    private static final String TAG = "ScheduleViewLoaderLog";
    private static final boolean DEBUG = true;

    @Nullable
    private String mSchedulePath;
    @Nullable
    private Schedule mSchedule;

    private long mNearbyDay = System.currentTimeMillis();
    private int mRequest;

    ScheduleViewLoader(@NonNull Context context) {
        super(context);
    }

    /**
     * Запускает загрузчик.
     * @param schedulePath путь до расписания.
     * @param schedule расписание.
     * @param request тип запроса.
     * @param currentDate текущая отображаемая дата.
     */
    void reloadData(@Nullable String schedulePath, @Nullable Schedule schedule,
                    int request, long currentDate) {
        mSchedulePath = schedulePath;
        mSchedule = schedule;
        mRequest = request;
        mNearbyDay = currentDate;

        forceLoad();
    }

    @Nullable
    @Override
    public ScheduleDataView loadInBackground() {
        ScheduleDataView scheduleDataView = new ScheduleDataView();
        scheduleDataView.hasErrors = true;

        // если запрос на сохранение расписания
        if (mRequest == REQUEST_SAVE_SCHEDULE) {
            if (mSchedule == null) {
                scheduleDataView.exception = new NullPointerException("Schedule is null!");
                return scheduleDataView;
            }

            try {
                mSchedule.save(mSchedulePath);

            } catch (JSONException | IOException e) {
                e.printStackTrace();

                scheduleDataView.exception = e;
                return scheduleDataView;
            }
        }

        // если запрос на загрузку расписания
        if (mRequest == REQUEST_LOAD_SCHEDULE) {
            try {
                mSchedule = new Schedule();
                mSchedule.load(mSchedulePath);

            } catch (JSONException | IOException e) {
                e.printStackTrace();

                scheduleDataView.exception = e;
                return scheduleDataView;
            }
        }

        scheduleDataView.schedule = mSchedule;
        scheduleDataView.dayPairs = new ArrayList<>();
        scheduleDataView.dayTitles = new ArrayList<>();
        scheduleDataView.dayDates = new ArrayList<>();

        Calendar startDate = scheduleDataView.schedule.minDate();
        Calendar endDate = scheduleDataView.schedule.maxDate();

        // если расписание пусто
        if (startDate == null || endDate == null) {
            scheduleDataView.hasErrors = false;
            return scheduleDataView;
        }

        // получение текущей локали
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = getContext().getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = getContext().getResources().getConfiguration().locale;
        }

        // дата отображаемая сейчас
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTimeInMillis(mNearbyDay);
        long currentTime = normalizeDate(currentDate);
        long currentTimeDiff = System.currentTimeMillis();

        // дата текущего дня
        Calendar todayDate = Calendar.getInstance();
        long todayTime = normalizeDate(todayDate);
        long todayTimeDiff = System.currentTimeMillis();

        int i = 0;
        while (startDate.compareTo(endDate) <= 0) {
            scheduleDataView.dayPairs.add(scheduleDataView.schedule.pairsByDate(startDate));

            // близкая к предыдущей отображаемой позиции
            long currentDiff = Math.abs(startDate.getTimeInMillis() - currentTime);
            if (currentDiff < currentTimeDiff) {
                currentTimeDiff = currentDiff;
                scheduleDataView.currentShowPosition = i;
            }

            // близкая к текущему дню позиция
            long todayDiff = Math.abs(startDate.getTimeInMillis() - todayTime);
            if (todayDiff < todayTimeDiff) {
                todayTimeDiff = todayDiff;
                scheduleDataView.todayPosition = i;
            }

            // заголок дня
            String dayTitle = new SimpleDateFormat("EEEE, dd MMMM",
                    locale) .format(startDate.getTime());
            dayTitle = dayTitle.substring(0, 1).toUpperCase() + dayTitle.substring(1);
            scheduleDataView.dayTitles.add(dayTitle);

            // дата дня
            scheduleDataView.dayDates.add(startDate.getTimeInMillis());

            i++;
            startDate.add(Calendar.DAY_OF_MONTH, 1);

            // пропускаем воскресение
            if (startDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                startDate.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

        scheduleDataView.hasErrors = false;
        return scheduleDataView;
    }

    /**
     * Нормализует дату относительно полночи.
     * @param date дата.
     * @return время в миллисекундах.
     */
    private long normalizeDate(@NonNull Calendar date) {
        return new GregorianCalendar(
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)).getTimeInMillis();
    }

    /**
     * Результат загрузчика.
     */
    class ScheduleDataView {
        Schedule schedule;

        ArrayList<Long> dayDates;
        ArrayList<TreeSet<Pair>> dayPairs;
        ArrayList<String> dayTitles;

        int todayPosition = RecyclerView.NO_POSITION;
        int currentShowPosition = RecyclerView.NO_POSITION;

        boolean hasErrors = false;
        Exception exception = null;
    }
}
