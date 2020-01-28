package com.github.nikololoshka.pepegaschedule.schedule.model.pair;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class ClassroomPair extends AttributePair implements Comparable<ClassroomPair> {

    private String mClassroom;

    ClassroomPair() {
        mClassroom = "";
    }

    private ClassroomPair(Parcel in) {
        mClassroom = in.readString();
    }

    public ClassroomPair(ClassroomPair classroomPair) {
        mClassroom = classroomPair.mClassroom;
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

    public static ClassroomPair of(String s) {
        ClassroomPair classroomPair = new ClassroomPair();
        classroomPair.mClassroom = s;
        return classroomPair;
    }

    public String classroom() {
        return mClassroom;
    }

    @Override
    public void load(JSONObject loadObject) throws JSONException {
        mClassroom = loadObject.getString("classroom");
    }

    @Override
    public void save(JSONObject saveObject) throws JSONException {
        saveObject.put("classroom", mClassroom);
    }

    @Override
    public boolean isValid() {
        return !mClassroom.isEmpty();
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
