package com.github.nikololoshka.pepegaschedule.modulejournal.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Objects;

/**
 * Оценки студента за семестр.
 */
public class StudentMarks {

    public static final int NO_RATING = 0;

    private static final String RATING = "Рейтинг";
    private static final String ACCUMULATED_RATING = "Накопленный Рейтинг";

    /**
     * Дисциплины с оценками.
     */
    private HashMap<String, Discipline> mDisciplines;

    /**
     * Текущий рейтинг.
     */
    @Nullable
    private Integer mRating;

    /**
     * Накопленный рейтинг.
     */
    @Nullable
    private Integer mAccumulatedRating;


    public StudentMarks() {
        mDisciplines = new HashMap<>(10);
    }

    /**
     * Добавляет оценку в список оценок за семестр.
     * @param disciplineTitle название предмета.
     * @param type тип оценки.
     * @param value значение оценки.
     * @param factor коэффициент предмета.
     */
    public void addDisciplineMark(@NonNull String disciplineTitle, @NonNull String type, int value, double factor) {
        if (disciplineTitle.equals(RATING)) {
            mRating = value;
            return;
        }

        if (disciplineTitle.equals(ACCUMULATED_RATING)) {
            mAccumulatedRating = value;
            return;
        }

        if (mDisciplines.containsKey(disciplineTitle)) {
            Discipline discipline = mDisciplines.get(disciplineTitle);

            if (discipline != null) {
                discipline.setMark(MarkType.of(type), value);
                return;
            }
        }

        Discipline discipline = new Discipline();
        discipline.setDiscipline(disciplineTitle);
        discipline.setMark(MarkType.of(type), value);
        discipline.setFactor(factor);
        mDisciplines.put(disciplineTitle, discipline);
    }

    /**
     * Возвращает значение рейтинга. Если {@code null} значит не удалось
     * получить рейтинг. Если рейтинга нет (не проставлен) то {@link #NO_RATING}.
     * @return рейтинг.
     */
    @Nullable
    public Integer rating() {
        return mRating;
    }

    /**
     * Возвращает значение накопленного рейтинга. Если {@code null} значит не удалось
     * получить накопленный рейтинг. Если накопленного рейтинга нет
     * (не проставлен) то возвращается {@link #NO_RATING}.
     * @return накопленный рейтинг.
     */
    @Nullable
    public Integer accumulatedRating() {
        return mAccumulatedRating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentMarks marks = (StudentMarks) o;
        return mDisciplines.equals(marks.mDisciplines) &&
                Objects.equals(mRating, marks.mRating) &&
                Objects.equals(mAccumulatedRating, marks.mAccumulatedRating);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mDisciplines, mRating, mAccumulatedRating);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Discipline discipline : mDisciplines.values()) {
            builder.append(discipline).append("\n");
        }

        builder.append(RATING).append(": ").append(mRating);
        builder.append("\n");
        builder.append(ACCUMULATED_RATING).append(": ").append(mAccumulatedRating);

        return builder.toString();
    }
}
