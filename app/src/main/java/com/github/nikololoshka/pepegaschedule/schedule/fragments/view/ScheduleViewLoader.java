package com.github.nikololoshka.pepegaschedule.schedule.fragments.view;

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

public class ScheduleViewLoader
        extends AsyncTaskLoader<ScheduleViewLoader.DataView> {

    private String mSchedulePath;
    private Schedule mSchedule;

    ScheduleViewLoader(@NonNull Context context) {
        super(context);
    }

    protected void update(String schedulePath, Schedule schedule) {
        mSchedulePath = schedulePath;
        mSchedule = schedule;
        forceLoad();
    }

    @Nullable
    @Override
    public DataView loadInBackground() {
        DataView dataView = new DataView();

        if (mSchedule == null) {
            mSchedule = new Schedule();
            try {
                mSchedule.load(mSchedulePath);
            } catch (JSONException e) {
                e.printStackTrace();
                return dataView;
            }
        } else {
            try {
                mSchedule.save(mSchedulePath);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        dataView.schedule = mSchedule;
        dataView.daysPair = new ArrayList<>();
        dataView.daysFormat = new ArrayList<>();

        Calendar startDate = dataView.schedule.minDate();
        Calendar endDate = dataView.schedule.maxDate();

        if (startDate == null || endDate == null) {
            return dataView;
        }

        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = getContext().getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = getContext().getResources().getConfiguration().locale;
        }


        Calendar date = new GregorianCalendar();
        long currentTime = new GregorianCalendar(date.get(Calendar.YEAR),
                date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH)).getTimeInMillis();
        long currentTimeDiff = System.currentTimeMillis();

        int i = 0;
        while (startDate.compareTo(endDate) <= 0) {
            dataView.daysPair.add(dataView.schedule.pairsByDate(startDate));

            long timeDiff = Math.abs(startDate.getTimeInMillis() - currentTime);
            if (timeDiff < currentTimeDiff) {
                currentTimeDiff = timeDiff;
                dataView.correctIndex = i;
            }

            String dayFormat = new SimpleDateFormat("EEEE, dd MMMM",
                    locale) .format(startDate.getTime());
            dayFormat = dayFormat.substring(0, 1).toUpperCase() + dayFormat.substring(1);
            dataView.daysFormat.add(dayFormat);

            startDate.add(Calendar.DAY_OF_MONTH, 1);
            if (startDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                startDate.add(Calendar.DAY_OF_MONTH, 1);
            }

            i++;
        }

        return dataView;
    }

    class DataView {
        Schedule schedule;
        ArrayList<TreeSet<Pair>> daysPair;
        ArrayList<String> daysFormat;
        int correctIndex;
    }
}
