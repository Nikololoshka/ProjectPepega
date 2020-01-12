package com.github.nikololoshka.pepegaschedule.modulejournal.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.EnumMap;
import java.util.Objects;

/**
 * Дисциплина в модульном журнале.
 */
public class Discipline {

    public static final float NO_FACTOR = 0;
    public static final int NO_MARK = 0;

    /**
     * Название дисциплины.
     */
    private String mDisciplineTitle;

    /**
     * Оценки дисциплины.
     */
    private EnumMap<MarkType, Integer> mMarks;

    /**
     * Коэффициет предмета.
     */
    private double mFactor;

    Discipline() {
        mDisciplineTitle = "";
        mMarks = new EnumMap<>(MarkType.class);
        mFactor = 0;
    }

    /**
     * Добавляет оценку.
     * @param type тип оценки.
     * @param value значение оценки.
     */
    public void setMark(MarkType type, int value) {
        mMarks.put(type, value);
    }

    /**
     * Возвращает оценку соответсвующего типа.
     * Если оценки нет, то возвращается {@code null}.
     * @param type тип оценки.
     * @return оценка.
     */
    @Nullable
    public Integer mark(MarkType type) {
        return mMarks.get(type);
    }

    /**
     * Если название предмета не установлено то возвращается пустая строка.
     * @return название предмета.
     */
    @NonNull
    public String discipline() {
        return mDisciplineTitle;
    }

    /**
     * Устанавливает название предмета.
     * @param discipline название предмета.
     */
    public void setDiscipline(@NonNull String discipline) {
        mDisciplineTitle = discipline;
    }

    /**
     * Возвращает {@link #NO_FACTOR} если коэффициета нет.
     * @return коэффициент предмета.
     */
    public double factor() {
        return mFactor;
    }

    /**
     * Устанавливает коэффициент предмета.
     * @param factor коэффициент.
     */
    public void setFactor(double factor) {
        mFactor = factor < 0 ? NO_FACTOR : factor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Discipline that = (Discipline) o;
        return Double.compare(that.mFactor, mFactor) == 0 &&
                mDisciplineTitle.equals(that.mDisciplineTitle) &&
                mMarks.equals(that.mMarks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mDisciplineTitle, mMarks, mFactor);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(mDisciplineTitle);

        for (MarkType type : MarkType.values()) {
            Integer mark = mMarks.get(type);
            builder.append(" | ");

            if (mark == null) {
                builder.append("--");
            } else if (mark == NO_MARK) {
                builder.append("  ");
            } else {
                builder.append(mark);
            }
        }
        builder.append(" |");

        return builder.toString();
    }
}
