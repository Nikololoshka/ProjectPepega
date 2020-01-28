package com.github.nikololoshka.pepegaschedule.schedule.model.pair;

import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public abstract class DateItem implements Comparable<DateItem>, Parcelable {
    public abstract DayOfWeek dayOfWeek();
    public abstract String compactDate();
    public abstract String fullDate();
    public abstract Calendar minDate();
    public abstract Calendar maxDate();
    public abstract FrequencyEnum frequency();
    public abstract boolean intersect(DateItem dateItem);
    public abstract void load(JSONObject loadObject) throws JSONException;
    public abstract void save(JSONArray saveArray) throws JSONException;
}
