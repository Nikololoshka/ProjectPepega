package com.vereshchagin.nikolay.stankinschedule.schedule.view.paging;

import androidx.annotation.NonNull;

import com.vereshchagin.nikolay.stankinschedule.schedule.model.pair.Pair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
    private List<Pair> mPairs;
    /**
     * День.
     */
    @NonNull
    private Calendar mDay;


    ScheduleDayItem(@NonNull TreeSet<Pair> pairs, @NonNull Calendar day) {
        mPairs = new ArrayList<>(pairs);
        mDay = day;
    }

    @NonNull
    public List<Pair> pairs() {
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
