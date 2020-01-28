package com.github.nikololoshka.pepegaschedule.schedule.model.pair;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.TreeSet;

public class DatePair extends AttributePair implements Comparable<DatePair> {

    private TreeSet<DateItem> mDates;
    @Nullable
    private DayOfWeek mDayOfWeek;

    DatePair() {
        mDates = new TreeSet<>();
        mDayOfWeek = null;
    }

    private DatePair(Parcel in) {
        Parcelable[] dateItems = in.readParcelableArray(DateItem.class.getClassLoader());
        mDates = new TreeSet<>();
        if (dateItems != null) {
            for (Parcelable item : dateItems) {
                addDate((DateItem) item);
            }
        }
    }

    public static final Creator<DatePair> CREATOR = new Creator<DatePair>() {
        @Override
        public DatePair createFromParcel(Parcel in) {
            return new DatePair(in);
        }

        @Override
        public DatePair[] newArray(int size) {
            return new DatePair[size];
        }
    };

    public static DatePair of(Collection<? extends DateItem> items) {
        DatePair datePair = new DatePair();
        for (DateItem item : items) {
            datePair.addDate(item);
        }
        return datePair;
    }

    public static boolean possibleChangeDate(ArrayList<DateItem> dateItem,
                                             @Nullable DateItem removedItem, DateItem addedItem) {
        for (DateItem item : dateItem) {
            if (!Objects.equals(removedItem, item)) {
                if (item.intersect(addedItem) ||
                        item.dayOfWeek() != addedItem.dayOfWeek()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void addDate(DateItem dateItem) {
        if (mDayOfWeek == null) {
            mDayOfWeek = dateItem.dayOfWeek();
        }

        if (mDayOfWeek == dateItem.dayOfWeek()) {
            if (!intersect(dateItem)) {
                mDates.add(dateItem);
            } else {
                throw new IllegalArgumentException("Intersect date");
            }
        } else {
            throw new IllegalArgumentException("Not add date");
        }
    }

    public void removeDate(DateItem removedDateItem) {
        mDates.remove(removedDateItem);
    }

    public DayOfWeek dayOfWeek() {
        return mDayOfWeek;
    }

    public boolean intersect(DateItem dateItem) {
        for (DateItem item : mDates) {
            if (item.intersect(dateItem)) {
                return true;
            }
        }
        return false;
    }

    public boolean intersect(DatePair datePair) {
        for (DateItem firstDate : mDates) {
            for (DateItem secondDate : datePair.mDates) {
                if (firstDate.intersect(secondDate)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int count() {
        return mDates.size();
    }

    public Calendar minDate() {
        return mDates.first().minDate();
    }

    public Calendar maxDate() {
        return Collections.max(mDates, new Comparator<DateItem>() {
            @Override
            public int compare(DateItem o1, DateItem o2) {
                return o1.maxDate().compareTo(o2.maxDate());
            }
        }).maxDate();
    }

    @Override
    public void load(JSONObject loadObject) throws JSONException {
        JSONArray datesObject = loadObject.getJSONArray("dates");
        for (int i = 0; i < datesObject.length(); i++) {
            JSONObject dateObject = datesObject.getJSONObject(i);
            String frequency = dateObject.getString("frequency");

            if (frequency.equals(FrequencyEnum.ONCE.tag())) {
                DateSingle dateSingle = new DateSingle();
                dateSingle.load(dateObject);
                addDate(dateSingle);
            } else if (frequency.equals(FrequencyEnum.EVERY.tag())
                    || frequency.equals(FrequencyEnum.THROUGHOUT.tag())) {
                DateRange dateRange = new DateRange();
                dateRange.load(dateObject);
                addDate(dateRange);
            } else {
                throw new IllegalArgumentException("Not found correct frequency");
            }
        }
    }

    @Override
    public void save(JSONObject saveObject) throws JSONException {
        JSONArray dateArray = new JSONArray();
        for (DateItem dateItem : mDates) {
            dateItem.save(dateArray);
        }
        saveObject.put("dates", dateArray);
    }

    @Override
    public boolean isValid() {
        return !mDates.isEmpty();
    }

    @Override
    public int compareTo(DatePair o) {
        return mDates.first().compareTo(o.mDates.first());
    }

    public ArrayList<DateItem> toList() {
        return new ArrayList<>(mDates);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('[');

        Iterator iterable = mDates.iterator();
        while (iterable.hasNext()) {
            builder.append(iterable.next());
            if (iterable.hasNext()) {
                builder.append(", ");
            }
        }
        builder.append(']');
        return builder.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        DateItem[] items = mDates.toArray(new DateItem[0]);
        dest.writeParcelableArray(items, flags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatePair datePair = (DatePair) o;
        return Objects.equals(mDates, datePair.mDates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mDates, mDayOfWeek);
    }
}
