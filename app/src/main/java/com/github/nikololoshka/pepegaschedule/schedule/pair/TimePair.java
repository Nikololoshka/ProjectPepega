package com.github.nikololoshka.pepegaschedule.schedule.pair;

import android.os.Parcel;

import androidx.annotation.NonNull;

import com.github.nikololoshka.pepegaschedule.schedule.pair.exceptions.InvalidPairParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TimePair extends AttributePair {

    private String mStart;
    private String mEnd;
    private int mStartNumber;
    private int mEndNumber;
    private Integer mDuration;

    TimePair() {
        mStart = null;
        mEnd = null;
        mDuration = null;
    }

    private TimePair(Parcel in) {
        mStart = in.readString();
        mEnd = in.readString();
        calculateDuration();
        updateNumbers();
    }

    public TimePair(TimePair timePair) {
        mStart = timePair.mStart;
        mEnd = timePair.mEnd;
        mDuration = timePair.mDuration;
        updateNumbers();
    }

    public static final Creator<TimePair> CREATOR = new Creator<TimePair>() {
        @Override
        public TimePair createFromParcel(Parcel in) {
            return new TimePair(in);
        }

        @Override
        public TimePair[] newArray(int size) {
            return new TimePair[size];
        }
    };

    public static TimePair of(String start, String end) {
        TimePair timePair = new TimePair();
        timePair.setTime(start, end);
        return timePair;
    }

    private void setTime(String start, String end) {
        if (!timeStarts().contains(start) || !timeEnds().contains(end)) {
            throw new InvalidPairParseException("Not parse time: " + start + " - " + end);
        }
        mStart = start;
        mEnd = end;
        calculateDuration();
        updateNumbers();
    }

    public String start() {
        return mStart;
    }

    public String end() {
        return mEnd;
    }

    public int startNumber() {
        return mStartNumber;
    }

    public int endNumber() {
        return mEndNumber;
    }

    public Integer duration() {
        return mDuration;
    }

    private void calculateDuration() {
        mDuration = timeEnds().indexOf(mEnd) - timeStarts().indexOf(mStart) + 1;
    }

    private void updateNumbers() {
        mStartNumber = timeStarts().indexOf(mStart);
        mEndNumber = timeEnds().indexOf(mEnd);
    }

    public boolean intersect(TimePair o) {
        return ((mStartNumber >= o.mStartNumber) && (mEndNumber <= o.mEndNumber))
                || ((mStartNumber <= o.mStartNumber) && (mEndNumber >= o.mStartNumber))
                || ((mStartNumber <= o.mEndNumber) && (mEndNumber >= o.mEndNumber));
    }

    private static List<String> timeStarts() {
        return Collections.unmodifiableList(
                Arrays.asList("8:30", "10:20", "12:20", "14:10",
                              "16:00", "18:00", "19:40", "21:20"));
    }

    private static List<String> timeEnds() {
        return Collections.unmodifiableList(
                Arrays.asList("10:10", "12:00", "14:00", "15:50",
                              "17:40", "19:30", "21:10", "22:50"));
    }

    @Override
    public void load(JSONObject loadObject) throws JSONException {
        JSONObject timeObject = loadObject.getJSONObject("time");
        String start = timeObject.getString("start");
        String end = timeObject.getString("end");
        setTime(start, end);
    }

    @Override
    public void save(JSONObject saveObject) throws JSONException {
        JSONObject timeObject = new JSONObject();
        timeObject.put("start", mStart);
        timeObject.put("end", mEnd);
        saveObject.put("time", timeObject);
    }

    @Override
    public boolean isValid() {
        return (mStart != null) && (mEnd != null) && (mDuration != null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimePair timePair = (TimePair) o;
        return Objects.equals(mStart, timePair.mStart) &&
                Objects.equals(mEnd, timePair.mEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mStart, mEnd);
    }

    @NonNull
    @Override
    public String toString() {
        return mStart + "-" + mEnd;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mStart);
        dest.writeString(mEnd);
    }
}
