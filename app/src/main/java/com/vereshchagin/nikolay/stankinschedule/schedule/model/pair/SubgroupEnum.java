package com.vereshchagin.nikolay.stankinschedule.schedule.model.pair;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

/**
 * Перечисление подгруппы пары.
 */
public enum SubgroupEnum {

    /**
     * Подгруппа А.
     */
    @Expose
    A("A"),

    /**
     * Подгруппа Б.
     */
    @Expose
    B("B"),

    /**
     * Обшая пара.
     */
    @Expose
    COMMON("Common");


    @NonNull
    private String mTag;

    SubgroupEnum(@NonNull String tag) {
        mTag = tag;
    }

    /**
     * Возвращает значение подгруппы соотвествующие значению в строке.
     * @param value строка с подгруппой.
     * @return подгруппа.
     */
    @NonNull
    public static SubgroupEnum of(@NonNull String value) {
        for (SubgroupEnum subgroup : SubgroupEnum.values()) {
            if (subgroup.mTag.equals(value)) {
                return subgroup;
            }
        }

        throw new IllegalArgumentException("No parse subgroup: " + value);
    }

    @NonNull
    @Override
    public String toString() {
        return mTag;
    }
}
