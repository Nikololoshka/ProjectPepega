package com.vereshchagin.nikolay.stankinschedule.modulejournal.network.response;

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

    @NonNull
    @Override
    public String toString() {
        return String.format("[%s : %s : %s]", studentName(), group(), semesters());
    }
}
