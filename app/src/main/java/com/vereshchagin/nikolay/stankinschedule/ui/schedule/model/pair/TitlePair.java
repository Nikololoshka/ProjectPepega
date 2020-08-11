package com.vereshchagin.nikolay.stankinschedule.ui.schedule.model.pair;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.Objects;

/**
 * Название пары.
 */
public class TitlePair implements Comparable<TitlePair>, Parcelable {

    private static final String JSON_TAG = "title";

    @NonNull
    private String mTitle;

    public TitlePair(@NonNull String title) {
        mTitle = title;
    }

    private TitlePair(@NonNull Parcel in) {
        String title = in.readString();
        if (title == null) {
            throw new IllegalArgumentException("No parsable title pair: " + in);
        }

        mTitle = title;
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

    /**
     * @return название пары.
     */
    public String title() {
        return mTitle;
    }

    /**
     * Создает TitlePair из json объекта.
     * @param object json объект.
     * @return название пары.
     */
    public static TitlePair fromJson(@NonNull JsonObject object) {
        return new TitlePair(object.get(JSON_TAG).getAsString());
    }

    /**
     * Добавляет TitlePair в json объект.
     * @param object json объект.
     */
    public void toJson(@NonNull JsonObject object) {
        object.addProperty(JSON_TAG, mTitle);
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

    @NonNull
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
