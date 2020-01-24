package com.github.nikololoshka.pepegaschedule.modulejournal.view.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * Дисциплина в модульном журнале.
 */
public class Discipline {

    private static final float NO_FACTOR = 0;
    private static final int NO_MARK = 0;

    /**
     * Название дисциплины.
     */
    @SerializedName("title")
    @Expose
    private String mDisciplineTitle;

    /**
     * Оценки дисциплины.
     */
    @SerializedName("marks")
    @Expose
    private LinkedHashMap<MarkType, Integer> mMarks;

    /**
     * Коэффициет предмета.
     */
    @SerializedName("factor")
    @Expose
    private double mFactor;

    Discipline() {
        mDisciplineTitle = "";
        mMarks = new LinkedHashMap<>();
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
     * Создает список для отображения в таблице.
     * @return список оценокю
     */
    public List<String> createRowCells() {
        ArrayList<String> row = new ArrayList<>();
        for (MarkType type : MarkType.values()) {
            Integer mark = mMarks.get(type);

            if (mark == null) {
                row.add("");
            } else if (mark == NO_MARK) {
                row.add("  ");
            } else {
                row.add(String.valueOf(mark));
            }
        }
        row.add(String.valueOf(mFactor));

        return row;
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
