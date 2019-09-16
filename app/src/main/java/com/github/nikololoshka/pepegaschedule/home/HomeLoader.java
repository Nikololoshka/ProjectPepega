package com.github.nikololoshka.pepegaschedule.home;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.github.nikololoshka.pepegaschedule.schedule.Schedule;
import com.github.nikololoshka.pepegaschedule.schedule.ScheduleDay;
import com.github.nikololoshka.pepegaschedule.schedule.pair.Pair;
import com.github.nikololoshka.pepegaschedule.schedule.pair.SubgroupEnum;
import com.github.nikololoshka.pepegaschedule.settings.SchedulePreference;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TreeSet;


public class HomeLoader extends AsyncTaskLoader<HomeLoader.DataView> {

    HomeLoader(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    public DataView loadInBackground() {
        Calendar date = new GregorianCalendar();
        Calendar today = new GregorianCalendar(date.get(Calendar.YEAR),
                date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));

        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = getContext().getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = getContext().getResources().getConfiguration().locale;
        }

        DataView dataView = new DataView();
        dataView.changeCount = SchedulePreference.changeCount();

        String dayFormat = new SimpleDateFormat("EEEE, dd MMMM",
                locale) .format(today.getTime());
        dataView.today = dayFormat.substring(0, 1).toUpperCase() + dayFormat.substring(1);


        String favorite = SchedulePreference.favorite(getContext());
        if (favorite.isEmpty()) {
            return dataView;
        }

        dataView.favorite = favorite;
        String path = SchedulePreference.createPath(getContext(), favorite);

        Schedule schedule = new Schedule();
        try {
            schedule.load(path);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TreeSet<Pair> pairs = new TreeSet<>(new ScheduleDay.SortPairComparator());
        if (today.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            pairs = schedule.pairsByDate(today);

            final SubgroupEnum subgroup = SchedulePreference.subgroup(getContext());

            removeIf(pairs, new RemovePairPredicate() {
                @Override
                public boolean isRemove(Pair pair) {
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

        dataView.pairs = new ArrayList<>(pairs);
        return dataView;
    }

    private void removeIf(TreeSet<Pair> pairs, RemovePairPredicate predicate) {
        ArrayList<Pair> removed = new ArrayList<>();
        for (Pair pair : pairs) {
            if (predicate.isRemove(pair)) {
                removed.add(pair);
            }
        }
        pairs.removeAll(removed);
    }

    private interface RemovePairPredicate {
        boolean isRemove(Pair pair);
    }

    class DataView {
        @Nullable
        ArrayList<Pair> pairs;
        @Nullable
        String favorite;

        String today;
        long changeCount;
    }
}
