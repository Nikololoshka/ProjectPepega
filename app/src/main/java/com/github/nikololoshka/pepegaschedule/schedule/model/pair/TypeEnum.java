package com.github.nikololoshka.pepegaschedule.schedule.model.pair;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

/**
 * Перечисление типы пары.
 */
public enum TypeEnum {

    /**
     * Лекция.
     */
    @Expose
    LECTURE("Lecture"),

    /**
     * Семинар.
     */
    @Expose
    SEMINAR("Seminar"),

    /**
     * Лабораторное занятие.
     */
    @Expose
    LABORATORY("Laboratory");


    @NonNull
    private String mTag;

    TypeEnum(@NonNull String tag) {
        mTag = tag;
    }

    /**
     * Возвращает значение типа пары соотвествующие значению в строке.
     * @param value строка с типом пары.
     * @return тип пары.
     */
    @NonNull
    public static TypeEnum of(@NonNull String value) {
        for (TypeEnum type : TypeEnum.values()) {
            if (type.mTag.equals(value)) {
                return type;
            }
        }

        throw new IllegalArgumentException("No parse type: " + value);
    }

    @NonNull
    @Override
    public String toString() {
        return mTag;
    }
}
