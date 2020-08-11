package com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.Objects;

/**
 * Преподаватель пары.
 */
public class LecturerPair implements Comparable<LecturerPair>, Parcelable {

    private static final String JSON_TAG = "lecturer";

    @NonNull
    private String mLecturer;

    public LecturerPair(@NonNull String lecturer) {
        mLecturer = lecturer;
    }

    private LecturerPair(@NonNull Parcel in) {
        String lecturer = in.readString();
        if (lecturer == null) {
            throw new IllegalArgumentException("No parsable lecturer pair: " + in);
        }

        mLecturer = lecturer;
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

    /**
     * @return преподаватель.
     */
    @NonNull
    public String lecturer() {
        return mLecturer;
    }

    /**
     * Создает LecturerPair из json объекта.
     * @param object json объект.
     * @return преподаватель пары.
     */
    public static LecturerPair fromJson(@NonNull JsonObject object) {
        return new LecturerPair(object.get(JSON_TAG).getAsString());
    }

    /**
     * Добавляет LecturerPair в json объект.
     * @param object json объект.
     */
    public void toJson(@NonNull JsonObject object) {
        object.addProperty(JSON_TAG, mLecturer);
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

    @NonNull
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
