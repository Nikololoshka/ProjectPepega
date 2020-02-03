package com.github.nikololoshka.pepegaschedule.schedule.model.pair;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

/**
 * Периодичность дат пары.
 */
public enum FrequencyEnum {

    /**
     * Единожды.
     */
    @Expose
    ONCE("once", 1),

    /**
     * Каждую неделю.
     */
    @Expose
    EVERY("every", 7),

    /**
     * Через неделю.
     */
    @Expose
    THROUGHOUT("throughout", 14);


    @NonNull
    private String mTag;
    private int mPeriod;

    FrequencyEnum(@NonNull String tag, int period) {
        mTag = tag;
        mPeriod = period;
    }

    /**
     * @return значение периодичности даты.
     */
    public int period() {
        return mPeriod;
    }

    /**
     * Возвращает значение периодичности даты пары соотвествующие значению в строке.
     * @param value периодичность даты в строке.
     * @return периодичность даты.
     */
    @NonNull
    public static FrequencyEnum of(@NonNull String value) {
        for (FrequencyEnum frequency : FrequencyEnum.values()) {
            if (frequency.mTag.equals(value)) {
                return frequency;
            }
        }

        throw new IllegalArgumentException("No parse frequency: " + value);
    }

    @NonNull
    @Override
    public String toString() {
        return mTag;
    }
}