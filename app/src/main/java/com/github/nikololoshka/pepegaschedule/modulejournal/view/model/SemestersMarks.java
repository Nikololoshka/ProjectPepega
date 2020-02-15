package com.github.nikololoshka.pepegaschedule.modulejournal.view.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.nikololoshka.pepegaschedule.modulejournal.network.ModuleJournalError;
import com.github.nikololoshka.pepegaschedule.modulejournal.network.response.MarkResponse;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

/**
 * Оценки студента за семестр.
 */
public class SemestersMarks {

    public static final String RATING = "Рейтинг";
    public static final String ACCUMULATED_RATING = "Накопленный Рейтинг";

    private static final String SEMESTERS_FOLDER = "semesters_data";

    /**
     * Дисциплины с оценками.
     */
    @NonNull
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
     * Время получения оценок.
     */
    @NonNull
    @SerializedName("time")
    @Expose
    private Calendar mTime;

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

    /**
     * Ошибка из-за которой не удалось получить данные.
     */
    @Expose(serialize = false, deserialize = false)
    @Nullable
    private ModuleJournalError mError;
    /**
     * Из кэша ли данные.
     */
    @Expose(serialize = false, deserialize = false)
    private boolean mIsCache;


    public SemestersMarks() {
        mDisciplines = new ArrayList<>(10);
        mTime = new GregorianCalendar();
        mError = null;
        mIsCache = false;
    }

    /**
     * Создает объект с оценками за семестр из ответа от сервера.
     * @param marksResponse ответ о сервера.
     * @return объект с оценками за семестр.
     */
    @NonNull
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
    private void addDisciplineMark(@NonNull String disciplineTitle, @NonNull String type, int value, double factor) {
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
    @NonNull
    public List<String> createColumnsData() {
        return Arrays.asList("М1", "М2", "К", "З", "Э", "К");
    }

    /**
     * Создает список списков ячеек с оценками.
     * @return список списков оценок.
     */
    @NonNull
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

    /**
     * @return время получения оценок.
     */
    @NonNull
    public Calendar time() {
        return mTime;
    }

    /**
     * Загружает оценки из кэша.
     * @param semester название семестра, которые необходимо загрузить.
     * @param cacheDirectory директория с кэшом приложения.
     * @return оценки за семестр из кэша.
     */
    @Nullable
    public static SemestersMarks loadCacheData(@NonNull String semester, @Nullable File cacheDirectory) {
        if (cacheDirectory == null) {
            return null;
        }

        File cacheFile = FileUtils.getFile(cacheDirectory,SEMESTERS_FOLDER, semester + ".json");
        if (!cacheFile.exists()) {
            return null;
        }

        try {
            String json = FileUtils.readFileToString(cacheFile, StandardCharsets.UTF_8);
            return new GsonBuilder()
                    .registerTypeAdapter(MarkType.class, new MarkType.MarkDeserialize())
                    .create()
                    .fromJson(json, SemestersMarks.class);

        } catch (IOException | JsonSyntaxException ignored) {

        }

        return null;
    }

    /**
     * Сохраняет оценки в кэш.
     * @param marks оценки за семестр.
     * @param semester название семестра.
     * @param cacheDirectory директория с кэшом приложения.
     */
    public static void saveCacheData(@NonNull SemestersMarks marks, @NonNull String semester, @Nullable File cacheDirectory) {
        if (cacheDirectory == null) {
            return;
        }

        File cacheFile = FileUtils.getFile(cacheDirectory,SEMESTERS_FOLDER, semester + ".json");
        String json = new GsonBuilder()
                .registerTypeAdapter(MarkType.class, new MarkType.MarkSerialize())
                .create()
                .toJson(marks);

        try {
            FileUtils.writeStringToFile(cacheFile, json, StandardCharsets.UTF_8, false);
        } catch (IOException ignored) {

        }
    }

    /**
     * Удаляет за кэшированные данные.
     * @param cacheDirectory директория с кэшом приложения.
     */
    public static void clearCacheData(@Nullable File cacheDirectory) {
        if (cacheDirectory == null) {
            return;
        }
        File cacheDir = FileUtils.getFile(cacheDirectory,SEMESTERS_FOLDER);

        FileUtils.deleteQuietly(cacheDir);
    }

    /**
     * @return ошибка во время получения данных.
     */
    @Nullable
    public ModuleJournalError error() {
        return mError;
    }

    /**
     * Устанавливает ошибку во время получения данных.
     * @param error ошибка.
     */
    public void setError(@Nullable ModuleJournalError error) {
        mError = error;
    }

    /**
     * Пуст ли семестр.
     * @return true - пуст, иначе false.
     */
    public boolean isEmpty() {
        return mDisciplines.isEmpty();
    }

    /**
     * Из кэша ли данные.
     * @return true - из кэша, иначе false.
     */
    public boolean isCache() {
        return mIsCache;
    }

    /**
     * Устанавливает были ли загружены данные из кэша.
     * @param cache из кэша ли.
     */
    public void setCache(boolean cache) {
        mIsCache = cache;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SemestersMarks marks = (SemestersMarks) o;
        return Objects.equals(mDisciplines, marks.mDisciplines) &&
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
