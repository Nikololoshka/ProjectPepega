package com.github.nikololoshka.pepegaschedule.schedule.myschedules;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.github.nikololoshka.pepegaschedule.settings.SchedulePreference;

import java.util.List;

public class MySchedulesLoader extends AsyncTaskLoader<MySchedulesLoader.DataView> {

    MySchedulesLoader(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    public MySchedulesLoader.DataView loadInBackground() {
        DataView dataView = new DataView();
        dataView.changeCount = SchedulePreference.changeCount();
        dataView.schedules = SchedulePreference.schedules(getContext());
        dataView.favorite = SchedulePreference.favorite(getContext());
        return dataView;
    }

    class DataView {
        List<String> schedules;
        String favorite;
        long changeCount;
    }
}
