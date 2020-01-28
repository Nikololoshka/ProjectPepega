package com.github.nikololoshka.pepegaschedule.schedule.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.github.nikololoshka.pepegaschedule.schedule.model.exceptions.InvalidAddPairException;
import com.github.nikololoshka.pepegaschedule.schedule.model.pair.DateSingle;
import com.github.nikololoshka.pepegaschedule.schedule.model.pair.Pair;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Objects;
import java.util.TreeSet;


public class ScheduleDay implements Parcelable {

    // TreeSet for table view of schedule
    private ArrayList<Pair> mBufferedPairs;

    // for table view of schedule
    // private ArrayList<HashMap<Integer, ArrayList<pair>>> m_pairLines;

    ScheduleDay() {
        mBufferedPairs = new ArrayList<>();
    }

    private ScheduleDay(Parcel in) {
        mBufferedPairs = new ArrayList<>();

        Parcelable[] pairs = in.readParcelableArray(Pair.class.getClassLoader());
        if (pairs != null) {
            for (Parcelable pair : pairs) {
                mBufferedPairs.add((Pair) pair);
            }
        }
    }

    public static final Creator<ScheduleDay> CREATOR = new Creator<ScheduleDay>() {
        @Override
        public ScheduleDay createFromParcel(Parcel in) {
            return new ScheduleDay(in);
        }

        @Override
        public ScheduleDay[] newArray(int size) {
            return new ScheduleDay[size];
        }
    };

    static void possibleChangePair(ScheduleDay day, Pair removedPair, Pair addedPair) {
        for (Pair pair : day.mBufferedPairs) {
            if (!Objects.equals(removedPair, pair)) {
                if (addedPair.time().intersect(pair.time())) {
                    if (addedPair.date().intersect(pair.date())) {
                        if (addedPair.subgroup().isConcurrently(pair.subgroup())) {
                            throw new InvalidAddPairException(String.format("Not add pair. Conflict: '%s' and '%s'",
                                    addedPair.toString(), removedPair.toString()));
                        }
                    }
                }
            }
        }
    }

    public void save(JSONArray jsonArray) throws JSONException {
        for (Pair pair : mBufferedPairs) {
            jsonArray.put(pair.save());
        }
    }

    void addPair(Pair addedPair) {
        if (checkPossibleAdded(addedPair)) {
            mBufferedPairs.add(addedPair);
        } else {
            throw new InvalidAddPairException(String.format("Not add pair: '%s'",
                    addedPair.toString()));
        }
    }

    void removePair(Pair removedPair) {
        mBufferedPairs.remove(removedPair);
    }

    TreeSet<Pair> pairsByDate(Calendar date) {
        TreeSet<Pair> pairs = new TreeSet<>(new SortPairComparator());

        DateSingle dateSingle = new DateSingle(date);

        for (Pair pair : mBufferedPairs) {
            if (pair.date().intersect(dateSingle)) {
                pairs.add(pair);
            }
        }
        return pairs;
    }

    @Nullable
    Calendar minDay() {
        if (mBufferedPairs.isEmpty()) {
            return null;
        }

        Calendar result = null;
        for (Pair pair : mBufferedPairs) {
            Calendar min = pair.date().minDate();
            if (result != null) {
                if (min.compareTo(result) < 0) {
                    result = min;
                }
            } else {
                result = min;
            }
        }

        return result;
    }

    @Nullable
    Calendar maxDay() {
        if (mBufferedPairs.isEmpty()) {
            return null;
        }

        Calendar result = null;
        for (Pair pair : mBufferedPairs) {
            Calendar max = pair.date().maxDate();
            if (result != null) {
                if (max.compareTo(result) > 0) {
                    result = max;
                }
            } else {
                result = max;
            }
        }

        return result;
    }

    private boolean checkPossibleAdded(Pair addedPair) {
        for (Pair pair : mBufferedPairs) {
            if (addedPair.time().intersect(pair.time())) {
                if (addedPair.date().intersect(pair.date())) {
                    if (addedPair.subgroup().isConcurrently(pair.subgroup())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Pair[] pairs = mBufferedPairs.toArray(new Pair[0]);
        dest.writeParcelableArray(pairs, flags);
    }

    public static class SortPairComparator implements Comparator<Pair> {
        @Override
        public int compare(Pair o1, Pair o2) {
            if (o1.time().startNumber() == o2.time().startNumber()) {
                return o1.subgroup().subgroup().compareTo(o2.subgroup().subgroup());
            }
            return Integer.compare(o1.time().startNumber(),
                    o2.time().startNumber());
        }
    }
}
