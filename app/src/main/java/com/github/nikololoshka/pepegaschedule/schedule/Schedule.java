package com.github.nikololoshka.pepegaschedule.schedule;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.github.nikololoshka.pepegaschedule.schedule.pair.DayOfWeek;
import com.github.nikololoshka.pepegaschedule.schedule.pair.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Objects;
import java.util.Scanner;
import java.util.TreeSet;


public class Schedule implements Parcelable {

    private EnumMap<DayOfWeek, ScheduleDay> mWeek;

    @Nullable
    private Calendar mMinDate;
    @Nullable
    private Calendar mMaxDate;

    public Schedule() {
       mWeek = new EnumMap<>(DayOfWeek.class);

       for (DayOfWeek day: DayOfWeek.values()) {
           mWeek.put(day, new ScheduleDay());
       }
    }

    private Schedule(Parcel in) {
        mWeek = new EnumMap<>(DayOfWeek.class);

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            mWeek.put(dayOfWeek, (ScheduleDay) in.readParcelable(ScheduleDay.class.getClassLoader()));
        }
        mMinDate = (Calendar) in.readSerializable();
        mMaxDate = (Calendar) in.readSerializable();
    }

    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel in) {
            return new Schedule(in);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };

    public static void possibleChangePair(Schedule schedule, Pair removedPair, Pair addedPair) {
        DayOfWeek day = addedPair.date().dayOfWeek();
        ScheduleDay scheduleDay = Objects.requireNonNull(schedule.mWeek.get(day));
        ScheduleDay.possibleChangePair(scheduleDay, removedPair, addedPair);
    }

    public boolean load(String filepath) throws JSONException {
        try (FileReader fileReader = new FileReader(filepath)) {
            Scanner scanner = new Scanner(fileReader);

            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
            }

            JSONArray jsonPairs = new JSONArray(builder.toString());
            for (int i = 0; i < jsonPairs.length(); i++) {
                JSONObject jsonPair = jsonPairs.getJSONObject(i);
                Pair pair = new Pair();
                pair.load(jsonPair);
                addPair(pair);
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean load(InputStream stream) throws JSONException {
        Scanner scanner = new Scanner(stream);

        StringBuilder builder = new StringBuilder();
        while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine());
        }

        JSONArray jsonPairs = new JSONArray(builder.toString());
        for (int i = 0; i < jsonPairs.length(); i++) {
            JSONObject jsonPair = jsonPairs.getJSONObject(i);
            Pair pair = new Pair();
            pair.load(jsonPair);
            addPair(pair);
        }

        return false;
    }

    public boolean save(String filepath) throws JSONException {
        try (FileWriter fileWriter = new FileWriter(filepath)) {
            JSONArray jsonPairs = new JSONArray();
            for (ScheduleDay day : mWeek.values()) {
                day.save(jsonPairs);
            }
            fileWriter.write(jsonPairs.toString(4));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean save(OutputStream stream) throws JSONException {
        try (PrintWriter writer = new PrintWriter(stream)) {
            JSONArray jsonPairs = new JSONArray();
            for (ScheduleDay day : mWeek.values()) {
                day.save(jsonPairs);
            }
            writer.write(jsonPairs.toString(4));
        }
        return true;
    }

    public void addPair(@Nullable Pair addedPair) {
        if (addedPair == null) {
            return;
        }
        DayOfWeek day = addedPair.date().dayOfWeek();
        Objects.requireNonNull(mWeek.get(day)).addPair(addedPair);
        updateMinMaxDate(addedPair);
    }

    public void removePair(@Nullable Pair removedPair) {
        if (removedPair == null) {
            return;
        }
        DayOfWeek day = removedPair.date().dayOfWeek();
        Objects.requireNonNull(mWeek.get(day)).removePair(removedPair);
        updateMinMaxDate();
    }

    public TreeSet<Pair> pairsByDate(Calendar date) {
        ScheduleDay day = Objects.requireNonNull(mWeek.get(DayOfWeek.valueOf(date)));
        return day.pairsByDate(date);
    }

    @Nullable
    public Calendar minDate() {
        return mMinDate != null ? (Calendar) mMinDate.clone() : null;
    }

    @Nullable
    public Calendar maxDate() {
        return mMaxDate != null ? (Calendar) mMaxDate.clone() : null;
    }

    private void updateMinMaxDate(Pair pair) {
        if (mMinDate == null || mMaxDate == null) {
            mMinDate = pair.date().minDate();
            mMaxDate = pair.date().maxDate();
            return;
        }

        if (pair.date().minDate().compareTo(mMinDate) < 0) {
            mMinDate = pair.date().minDate();
        }

        if (pair.date().maxDate().compareTo(mMaxDate) > 0) {
            mMaxDate = pair.date().maxDate();
        }
    }

    private void updateMinMaxDate() {
        mMinDate = Collections.min(mWeek.values(), new Comparator<ScheduleDay>() {
            @Override
            public int compare(ScheduleDay o1, ScheduleDay o2) {
                Calendar first = o1.minDay();
                Calendar second = o2.minDay();
                return first != null ? first.compareTo(second) : 0;
            }
        }).minDay();

        mMaxDate = Collections.max(mWeek.values(), new Comparator<ScheduleDay>() {
            @Override
            public int compare(ScheduleDay o1, ScheduleDay o2) {
                Calendar first = o1.maxDay();
                Calendar second = o2.maxDay();
                return first != null ? first.compareTo(second) : 0;
            }
        }).maxDay();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            dest.writeParcelable(mWeek.get(dayOfWeek), flags);
        }
        dest.writeSerializable(mMinDate);
        dest.writeSerializable(mMaxDate);
    }
}
