package com.github.nikololoshka.pepegaschedule.schedule.pair;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class LecturerPair extends AttributePair implements Comparable<LecturerPair> {

    private String mLecturer;

    LecturerPair() {
        mLecturer = "";
    }

    private LecturerPair(Parcel in) {
        mLecturer = in.readString();
    }

    public LecturerPair(LecturerPair lecturerPair) {
        mLecturer = lecturerPair.mLecturer;
    }

    public static final Creator<LecturerPair> CREATOR = new Creator<LecturerPair>() {
        @Override
        public LecturerPair createFromParcel(Parcel in) {
            return new LecturerPair(in);
        }

        @Override
        public LecturerPair[] newArray(int size) {
            return new LecturerPair[size];
        }
    };

    public static LecturerPair of(String s) {
        LecturerPair lecturerPair = new LecturerPair();
        lecturerPair.mLecturer = s.trim();
        return lecturerPair;
    }

    public String lecturer() {
        return mLecturer;
    }

    @Override
    public void load(JSONObject loadObject) throws JSONException {
        mLecturer = loadObject.getString("lecturer");
    }

    @Override
    public void save(JSONObject saveObject) throws JSONException {
        saveObject.put("lecturer", mLecturer);
    }

    @Override
    public boolean isValid() {
        return !mLecturer.isEmpty();
    }

    @Override
    public int compareTo(LecturerPair o) {
        return mLecturer.compareTo(o.mLecturer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LecturerPair that = (LecturerPair) o;
        return Objects.equals(mLecturer, that.mLecturer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mLecturer);
    }

    @Override
    public String toString() {
        return mLecturer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mLecturer);
    }
}
