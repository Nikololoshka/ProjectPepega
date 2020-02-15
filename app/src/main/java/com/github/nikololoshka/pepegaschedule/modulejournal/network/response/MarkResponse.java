package com.github.nikololoshka.pepegaschedule.modulejournal.network.response;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Locale;

/**
 * POJO-класс для списка "marks" ответа от сервера.
 */
public class MarkResponse {

    @SerializedName("factor")
    @Expose
    private Double mFactor;

    @SerializedName("title")
    @Expose
    private String mDiscipline;

    @SerializedName("num")
    @Expose
    private String mType;

    @SerializedName("value")
    @Expose
    private Integer mValue;

    @NonNull
    public Double factor() {
        return mFactor;
    }

    @NonNull
    public String discipline() {
        return mDiscipline;
    }

    @NonNull
    public String type() {
        return mType;
    }

    @NonNull
    public Integer value() {
        return mValue;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s: %d, %s, %s", mDiscipline, mValue, mType, mFactor);
    }
}
