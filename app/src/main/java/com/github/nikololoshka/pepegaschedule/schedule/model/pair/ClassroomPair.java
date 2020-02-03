package com.github.nikololoshka.pepegaschedule.schedule.model.pair;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.Objects;

/**
 * Аудитория пары.
 */
public class ClassroomPair implements Comparable<ClassroomPair>, Parcelable {

    private final static String JSON_TAG = "classroom";

    @NonNull
    private String mClassroom;

    public ClassroomPair(@NonNull String classroom) {
        mClassroom = classroom;
    }

    private ClassroomPair(@NonNull Parcel in) {
        String classroom = in.readString();
        if (classroom == null) {
            throw new IllegalArgumentException("No parsable classroom pair: " + in);
        }

        mClassroom = classroom;
    }

    public static final Creator<ClassroomPair> CREATOR = new Creator<ClassroomPair>() {
        @Override
        public ClassroomPair createFromParcel(Parcel in) {
            return new ClassroomPair(in);
        }

        @Override
        public ClassroomPair[] newArray(int size) {
            return new ClassroomPair[size];
        }
    };

    /**
     * @return аудитория.
     */
    @NonNull
    public String classroom() {
        return mClassroom;
    }

    /**
     * Создает ClassroomPair из json объекта.
     * @param object json объект.
     * @return аудитория пары.
     */
    public static ClassroomPair fromJson(@NonNull JsonObject object) {
        return new ClassroomPair(object.get(JSON_TAG).getAsString());
    }

    /**
     * Добавляет ClassroomPair в json объект.
     * @param object json объект.
     */
    public void toJson(@NonNull JsonObject object) {
        object.addProperty(JSON_TAG, mClassroom);
    }

    @Override
    public int compareTo(ClassroomPair o) {
        return mClassroom.compareTo(o.mClassroom);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassroomPair that = (ClassroomPair) o;
        return Objects.equals(mClassroom, that.mClassroom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mClassroom);
    }

    @NonNull
    @Override
    public String toString() {
        return mClassroom;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mClassroom);
    }
}
