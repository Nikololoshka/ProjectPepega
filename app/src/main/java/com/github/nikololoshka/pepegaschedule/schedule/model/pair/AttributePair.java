package com.github.nikololoshka.pepegaschedule.schedule.model.pair;

import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class AttributePair implements Parcelable {
    public abstract void load(JSONObject loadObject) throws JSONException;
    public abstract void save(JSONObject saveObject) throws JSONException;
    public abstract boolean isValid();
}
