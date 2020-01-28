package com.github.nikololoshka.pepegaschedule.schedule.model.pair;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;


public class TitlePair extends AttributePair implements Comparable<TitlePair> {

    private String mTitle;

    TitlePair() {
        mTitle = "";
    }

    private TitlePair(Parcel in) {
        mTitle = in.readString();
    }

    public TitlePair(TitlePair titlePair) {
        mTitle = titlePair.mTitle;
    }

    public static final Creator<TitlePair> CREATOR = new Creator<TitlePair>() {
        @Override
        public TitlePair createFromParcel(Parcel in) {
            return new TitlePair(in);
        }

        @Override
        public TitlePair[] newArray(int size) {
            return new TitlePair[size];
        }
    };

    public static TitlePair of(String s) {
        TitlePair titlePair = new TitlePair();
        titlePair.mTitle = s.trim();
        return titlePair;
    }

    public String title() {
        return mTitle;
    }

    @Override
    public void load(JSONObject loadObject) throws JSONException {
        mTitle = loadObject.getString("title");
    }

    @Override
    public void save(JSONObject saveObject) throws JSONException {
        saveObject.put("title", mTitle);
    }

    @Override
    public boolean isValid() {
        return !mTitle.isEmpty();
    }

    @Override
    public int compareTo(TitlePair o) {
        return mTitle.compareTo(o.mTitle);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TitlePair titlePair = (TitlePair) o;
        return Objects.equals(mTitle, titlePair.mTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mTitle);
    }

    @Override
    public String toString() {
        return mTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
    }
}
