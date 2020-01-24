package com.github.nikololoshka.pepegaschedule.modulejournal.view.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.nikololoshka.pepegaschedule.modulejournal.network.MarkResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Оценки студента за семестр.
 */
public class SemestersMarks {

    public static final String RATING = "Рейтинг";
    public static final String ACCUMULATED_RATING = "Накопленный Рейтинг";

    /**
     * Дисциплины с оценками.
     */
    @SerializedName("disciplines")
    @Expose
    private ArrayList<Discipline> mDisciplines;

    /**
     * Текущий рейтинг.
     */
    @Nullable
    @SerializedName("rating")
    @Expose
    private Integer mRating;

    /**
     * Накопленный рейтинг.
     */
    @Nullable
    @SerializedName("accumulated_rating")
    @Expose
    private Integer mAccumulatedRating;

    /**
     * Время, получения оценок.
     */
    @SerializedName("time")
    @Expose
    private long mTime;

    /**
     * Кэш заголовков строк.
     */
    @Expose(serialize = false, deserialize = false)
    @Nullable
    private List<String> mRowsData;

    /**
     * Кэш ячеек с оценками.
     */
    @Expose(serialize = false, deserialize = false)
    @Nullable
    private List<List<String>> mCellsData;


    public SemestersMarks() {
        mDisciplines = new ArrayList<>(10);
        mTime = System.currentTimeMillis();
    }

    /**
     * Создает объект с оценками за семестр из ответа от сервера.
     * @param marksResponse ответ о сервера.
     * @return объект с оценками за семестр.
     */
    public static SemestersMarks fromResponse(@Nullable List<MarkResponse> marksResponse) {
        SemestersMarks semestersMarks = new SemestersMarks();

        if (marksResponse != null) {
            for (MarkResponse markResponse : marksResponse) {
                semestersMarks.addDisciplineMark(markResponse.discipline(),
                        markResponse.type(), markResponse.value(), markResponse.factor());
            }
        }

        return semestersMarks;
    }

    /**
     * Добавляет оценку в список оценок за семестр.
     * @param disciplineTitle название предмета.
     * @param type тип оценки.
     * @param value значение оценки.
     * @param factor коэффициент предмета.
     */
    public void addDisciplineMark(@NonNull String disciplineTitle, @NonNull String type, int value, double factor) {
        mRowsData = null;
        mCellsData = null;

        if (disciplineTitle.equals(RATING)) {
            mRating = value;
            return;
        }

        if (disciplineTitle.equals(ACCUMULATED_RATING)) {
            mAccumulatedRating = value;
            return;
        }

        for (Discipline discipline : mDisciplines) {
            if (discipline.discipline().equals(disciplineTitle)) {
                discipline.setMark(MarkType.of(type), value);
                return;
            }
        }

        Discipline discipline = new Discipline();
        discipline.setDiscipline(disciplineTitle);
        discipline.setMark(MarkType.of(type), value);
        discipline.setFactor(factor);

        mDisciplines.add(discipline);
        Collections.sort(mDisciplines, new Comparator<Discipline>() {
            @Override
            public int compare(Discipline o1, Discipline o2) {
                return o1.discipline().compareTo(o2.discipline());
            }
        });
    }

    /**
     * Создает список с заголовками строк таблицы.
     * @return список заголовков строк.
     */
    public List<String> createRowsData() {
        if (mRowsData != null) {
            return mRowsData;
        }

        ArrayList<String> rowsData = new ArrayList<>(mDisciplines.size());
        for (Discipline discipline : mDisciplines) {
            rowsData.add(discipline.discipline());
        }

        if (mRating != null) {
            rowsData.add(RATING);
        }
        if (mAccumulatedRating != null) {
            rowsData.add(ACCUMULATED_RATING);
        }

        mRowsData = rowsData;
        return rowsData;
    }

    /**
     * Создает список с заголовками столбцов таблицы.
     * @return список заголовков столбцов.
     */
    public List<String> createColumnsData() {
        return Arrays.asList("М1", "М2", "К", "З", "Э", "К");
    }

    /**
     * Создает список списков ячеек с оценками.
     * @return список списков оценок.
     */
    public List<List<String>> createCellsData() {
        if (mCellsData != null) {
            return mCellsData;
        }

        List<List<String>> cellsData = new ArrayList<>();
        for (Discipline discipline : mDisciplines) {
            cellsData.add(discipline.createRowCells());
        }

        int count = createColumnsData().size();
        if (mRating != null) {
            ArrayList<String> list = new ArrayList<>(count);
            list.add(mRating == 0 ? "  " : String.valueOf(mRating));

            for (int i = 0; i < count - 1; i++) {
                list.add("");
            }

            cellsData.add(list);
        }
        if (mAccumulatedRating != null) {
            ArrayList<String> list = new ArrayList<>(count);
            list.add(mAccumulatedRating == 0 ? "  " : String.valueOf(mAccumulatedRating));

            for (int i = 0; i < count - 1; i++) {
                list.add("");
            }

            cellsData.add(list);
        }

        mCellsData = cellsData;

        return cellsData;
    }

    public long time() {
        return mTime;
    }

    /**
     * Возвращает значение рейтинга. Если {@code null} значит не удалось
     * получить рейтинг. Если рейтинга нет (не проставлен) то 0.
     * @return рейтинг.
     */
    @Nullable
    public Integer rating() {
        return mRating;
    }

    /**
     * Возвращает значение накопленного рейтинга. Если {@code null} значит не удалось
     * получить накопленный рейтинг. Если накопленного рейтинга нет
     * (не проставлен) то возвращается 0.
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
        SemestersMarks marks = (SemestersMarks) o;
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

        for (Discipline discipline : mDisciplines) {
            builder.append(discipline).append("\n");
        }

        builder.append(RATING).append(": ").append(mRating);
        builder.append("\n");
        builder.append(ACCUMULATED_RATING).append(": ").append(mAccumulatedRating);

        return builder.toString();
    }
}
