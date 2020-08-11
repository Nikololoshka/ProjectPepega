package com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Время пары.
 */
public class TimePair implements Parcelable {

    private static final String JSON_TAG = "time";
    private static final String JSON_START = "start";
    private static final String JSON_END = "end";

    /**
     * Время начала.
     */
    @NonNull
    private String mStart;

    /**
     * Время конца.
     */
    @NonNull
    private String mEnd;

    private int mStartNumber;
    private int mEndNumber;

    public TimePair(@NonNull String start, @NonNull String end) {
        if (!timeStarts().contains(start) || !timeEnds().contains(end)) {
            throw new IllegalArgumentException("Not parse time: " + start + " - " + end);
        }

        mStart = start;
        mEnd = end;

        updateNumbers();
    }

    private TimePair(@NonNull Parcel in) {
        String start = in.readString();
        String end = in.readString();
        if (start == null || end == null) {
            throw new IllegalArgumentException("No parsable time pair: " + in);
        }

        mStart = start;
        mEnd = end;

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

    /**
     * Устанавливает время пары.
     * @param start начало пары.
     * @param end конец пары.
     */
    private void setTime(@NonNull String start, @NonNull String end) {
        if (!timeStarts().contains(start) || !timeEnds().contains(end)) {
            throw new IllegalArgumentException("Not parse time: " + start + " - " + end);
        }

        mStart = start;
        mEnd = end;
    }

    /**
     * @return начало пары.
     */
    @NonNull
    public String start() {
        return mStart;
    }

    /**
     * @return конец пары.
     */
    @NonNull
    public String end() {
        return mEnd;
    }

    /**
     * @return номер начала пары.
     */
    public int startNumber() {
        return mStartNumber;
    }

    /**
     * @return номер конца пары.
     */
    public int endNumber() {
        return mEndNumber;
    }

    /**
     * @return продолжительность пары.
     */
    public int duration() {
        return timeEnds().indexOf(mEnd) - timeStarts().indexOf(mStart) + 1;
    }

    /**
     * Обновляет номера начала и конца пары.
     */
    private void updateNumbers() {
        mStartNumber = timeStarts().indexOf(mStart);
        mEndNumber = timeEnds().indexOf(mEnd);
    }

    /**
     * Определяет, пересекается ли время пар.
     * @param o другая пара.
     * @return true если время текущей и сравниваемой пары пересекается, иначе false.
     */
    public boolean intersect(@NonNull TimePair o) {
        return ((mStartNumber >= o.mStartNumber) && (mEndNumber <= o.mEndNumber))
                || ((mStartNumber <= o.mStartNumber) && (mEndNumber >= o.mStartNumber))
                || ((mStartNumber <= o.mEndNumber) && (mEndNumber >= o.mEndNumber));
    }

    /**
     * @return список времени начало пар.
     */
    private static List<String> timeStarts() {
        return Collections.unmodifiableList(
                Arrays.asList("8:30", "10:20", "12:20", "14:10",
                              "16:00", "18:00", "19:40", "21:20"));
    }

    /**
     * @return список времени окончания пар.
     */
    private static List<String> timeEnds() {
        return Collections.unmodifiableList(
                Arrays.asList("10:10", "12:00", "14:00", "15:50",
                              "17:40", "19:30", "21:10", "22:50"));
    }

    /**
     * Создает TimePair из json объекта.
     * @param object json объект.
     * @return время пары.
     */
    public static TimePair fromJson(@NonNull JsonObject object) {
        JsonObject timeObject = object.getAsJsonObject(JSON_TAG);

        String start = timeObject.get(JSON_START).getAsString();
        String end = timeObject.get(JSON_END).getAsString();

        return new TimePair(start, end);
    }

    /**
     * Добавляет TimePair в json объект.
     * @param object json объект.
     */
    public void toJson(@NonNull JsonObject object) {
        JsonObject timeObject = new JsonObject();

        timeObject.addProperty(JSON_START, mStart);
        timeObject.addProperty(JSON_END, mEnd);

        object.add(JSON_TAG, timeObject);
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
