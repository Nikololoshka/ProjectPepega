package com.github.nikololoshka.pepegaschedule.modulejournal.network;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * POJO-класс "semesters" ответа от сервара.
 */
public class SemestersResponse {

    @SerializedName("surname")
    @Expose
    private String mSurname;

    @SerializedName("initials")
    @Expose
    private String mInitials;

    @SerializedName("stgroup")
    @Expose
    private String mGroup;

    @SerializedName("semesters")
    @Expose
    private ArrayList<String> mSemesters;

    /**
     * Время, получения информации.
     */
    @SerializedName("time")
    @Expose
    private long mTime;

    public SemestersResponse() {
        mTime = System.currentTimeMillis();
    }

    @NonNull
    public String studentName() {
        return mSurname + " " + mInitials;
    }

    @NonNull
    public String group() {
        return mGroup;
    }

    @NonNull
    public ArrayList<String> semesters() {
        return mSemesters;
    }

    public long time() {
        return mTime;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("[%s : %s : %s]", studentName(), group(), semesters());
    }
}
