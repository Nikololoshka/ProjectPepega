package com.github.nikololoshka.pepegaschedule.schedule.view;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.github.nikololoshka.pepegaschedule.schedule.Schedule;
import com.github.nikololoshka.pepegaschedule.schedule.pair.Pair;

import org.json.JSONException;

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

    public static final int REQUEST_SAVE_SCHEDULE = 1;
    public static final int REQUEST_LOAD_SCHEDULE = 2;

    private static final String TAG = "ScheduleViewLoaderLog";
    private static final boolean DEBUG = true;

    private String mSchedulePath;
    private Schedule mSchedule;
    private int mRequest;

    ScheduleViewLoader(@NonNull Context context) {
        super(context);
    }

    /**
     * Запускает загрузчик.
     * @param schedulePath - путь до расписания.
     * @param schedule - расписание.
     * @param request - тип запроса.
     */
    void reloadData(String schedulePath, @Nullable Schedule schedule, int request) {
        mSchedulePath = schedulePath;
        mSchedule = schedule;
        mRequest = request;

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
                // TODO: исправить обработку исключений
                mSchedule.save(mSchedulePath);

            } catch (JSONException e) {
                e.printStackTrace();

                scheduleDataView.exception = e;
                return scheduleDataView;
            }
        }

        // если запрос на загрузку расписания
        if (mRequest == REQUEST_LOAD_SCHEDULE) {
            try {
                // TODO: исправить обработку исключений
                mSchedule = new Schedule();
                mSchedule.load(mSchedulePath);
            } catch (JSONException e) {
                e.printStackTrace();

                scheduleDataView.exception = e;
                return scheduleDataView;
            }
        }

        scheduleDataView.schedule = mSchedule;
        scheduleDataView.daysPair = new ArrayList<>();
        scheduleDataView.daysFormat = new ArrayList<>();

        Calendar startDate = scheduleDataView.schedule.minDate();
        Calendar endDate = scheduleDataView.schedule.maxDate();

        // если расписание пусто
        if (startDate == null || endDate == null) {
            scheduleDataView.hasErrors = false;
            return scheduleDataView;
        }

        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = getContext().getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = getContext().getResources().getConfiguration().locale;
        }


        Calendar date = new GregorianCalendar();
        long currentTime = new GregorianCalendar(
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)).getTimeInMillis();

        long currentTimeDiff = System.currentTimeMillis();

        int i = 0;
        while (startDate.compareTo(endDate) <= 0) {
            scheduleDataView.daysPair.add(scheduleDataView.schedule.pairsByDate(startDate));

            long timeDiff = Math.abs(startDate.getTimeInMillis() - currentTime);
            if (timeDiff < currentTimeDiff) {
                currentTimeDiff = timeDiff;
                scheduleDataView.correctIndex = i;
            }

            String dayFormat = new SimpleDateFormat("EEEE, dd MMMM",
                    locale) .format(startDate.getTime());
            dayFormat = dayFormat.substring(0, 1).toUpperCase() + dayFormat.substring(1);
            scheduleDataView.daysFormat.add(dayFormat);

            startDate.add(Calendar.DAY_OF_MONTH, 1);
            if (startDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                startDate.add(Calendar.DAY_OF_MONTH, 1);
            }

            i++;
        }

        scheduleDataView.hasErrors = false;
        return scheduleDataView;
    }

    /**
     * Результата загрузчика.
     */
    class ScheduleDataView {
        Schedule schedule;

        ArrayList<TreeSet<Pair>> daysPair;
        ArrayList<String> daysFormat;
        int correctIndex;

        boolean hasErrors;
        Exception exception;
    }
}
