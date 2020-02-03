package com.github.nikololoshka.pepegaschedule.schedule.view.paging;

import androidx.annotation.NonNull;

import com.github.nikololoshka.pepegaschedule.schedule.model.pair.Pair;

import java.util.Calendar;
import java.util.Objects;
import java.util.TreeSet;

/**
 * POJO класс дня с парами.
 */
public class ScheduleDayItem {

    /**
     * Пары в дне.
     */
    @NonNull
    private TreeSet<Pair> mPairs;
    /**
     * День.
     */
    @NonNull
    private Calendar mDay;


    ScheduleDayItem(@NonNull TreeSet<Pair> pairs, @NonNull Calendar day) {
        mPairs = pairs;
        mDay = day;
    }

    @NonNull
    public TreeSet<Pair> pairs() {
        return mPairs;
    }

    @NonNull
    public Calendar day() {
        return mDay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleDayItem that = (ScheduleDayItem) o;
        return mPairs.equals(that.mPairs) && mDay.equals(that.mDay);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mPairs, mDay);
    }
}
