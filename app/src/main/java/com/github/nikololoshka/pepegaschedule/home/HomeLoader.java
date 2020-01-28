package com.github.nikololoshka.pepegaschedule.home;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.github.nikololoshka.pepegaschedule.schedule.model.Schedule;
import com.github.nikololoshka.pepegaschedule.schedule.model.ScheduleDay;
import com.github.nikololoshka.pepegaschedule.schedule.model.pair.Pair;
import com.github.nikololoshka.pepegaschedule.schedule.model.pair.SubgroupEnum;
import com.github.nikololoshka.pepegaschedule.settings.SchedulePreference;
import com.github.nikololoshka.pepegaschedule.utils.CommonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TreeSet;


public class HomeLoader extends AsyncTaskLoader<HomeLoader.LoadData> {

    private static final int DAY_COUNT = 5;

    HomeLoader(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    public LoadData loadInBackground() {
        // get today date
        Calendar date = new GregorianCalendar();
        Calendar dayNow = new GregorianCalendar(date.get(Calendar.YEAR),
                date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));

        // get locale
        Locale locale = CommonUtils.locale(getContext());

        // create loadData
        LoadData loadData = new LoadData();
        loadData.changeCount = SchedulePreference.changeCount();

        // set favorite schedule
        String favorite = SchedulePreference.favorite(getContext());
        if (favorite.isEmpty()) {
            return loadData;
        }
        loadData.favorite = favorite;

        // load schedule
        String path = SchedulePreference.createPath(getContext(), favorite);
        Schedule schedule = new Schedule();
        try {
            schedule.load(path);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error parsing schedule", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error loading schedule", Toast.LENGTH_SHORT).show();
        }

        // prepare for create days and titles
        dayNow.add(Calendar.DAY_OF_MONTH, -2);

        for (int i = 0; i < DAY_COUNT; i++) {
            TreeSet<Pair> pairs = new TreeSet<>(new ScheduleDay.SortPairComparator());
            if (dayNow.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                pairs = schedule.pairsByDate(dayNow);

                final SubgroupEnum subgroup = SchedulePreference.subgroup(getContext());

                removeIf(pairs, new RemovePairPredicate() {
                    @Override
                    public boolean isRemove(@NonNull Pair pair) {
                        switch (subgroup) {
                            case A:
                                return pair.subgroup().subgroup() == SubgroupEnum.B;
                            case B:
                                return pair.subgroup().subgroup() == SubgroupEnum.A;
                        }
                        return false;
                    }
                });
            }

            String dayFormat = new SimpleDateFormat("EEEE, dd MMMM",
                    locale).format(dayNow.getTime());
            String dayString = dayFormat.substring(0, 1).toUpperCase() + dayFormat.substring(1);

            loadData.days.add(new ArrayList<>(pairs));
            loadData.titles.add(dayString);

            dayNow.add(Calendar.DAY_OF_MONTH, 1);
        }

        return loadData;
    }

    private void removeIf(@NonNull TreeSet<Pair> pairs, @NonNull RemovePairPredicate predicate) {
        ArrayList<Pair> removed = new ArrayList<>();
        for (Pair pair : pairs) {
            if (predicate.isRemove(pair)) {
                removed.add(pair);
            }
        }
        pairs.removeAll(removed);
    }

    private interface RemovePairPredicate {
        boolean isRemove(@NonNull Pair pair);
    }

    class LoadData {
        @NonNull
        String favorite = "";
        @NonNull
        ArrayList<ArrayList<Pair>> days = new ArrayList<>();
        @NonNull
        ArrayList<String> titles = new ArrayList<>();

        long changeCount;
    }
}
