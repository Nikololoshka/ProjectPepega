package com.github.nikololoshka.pepegaschedule.modulejournal.view.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.nikololoshka.pepegaschedule.modulejournal.network.response.SemestersResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

/**
 * Информация о студенте.
 */
public class StudentData {

    private static final String STUDENT_FOLDER = "student_data";

    /**
     * Имя студента.
     */
    @SerializedName("student")
    @Expose
    @NonNull
    private String mStudentName;
    /**
     * Группа студента.
     */
    @SerializedName("group")
    @Expose
    @NonNull
    private String mStudentGroup;
    /**
     * Семестры студента.
     */
    @SerializedName("semesters")
    @Expose
    @NonNull
    private List<String> mSemesters;

    /**
     * Время получения информации.
     */
    @SerializedName("time")
    @Expose
    @NonNull
    private Calendar mTime;

    private StudentData(@NonNull String studentName, @NonNull String studentGroup,
                        @NonNull List<String> semesters) {
        mStudentName = studentName;
        mStudentGroup = studentGroup;
        mSemesters = semesters;
        mTime = new GregorianCalendar();
    }

    /**
     * Создает информацию о студенте из ответа от сервера.
     * @param response ответ от сервера.
     * @return информация о студенте.
     */
    @NonNull
    public static StudentData fromResponse(@NonNull SemestersResponse response) {
        return new StudentData(response.studentName(), response.group(), response.semesters());
    }

    /**
     * @return имя студента.
     */
    @NonNull
    public String name() {
        return mStudentName;
    }

    /**
     * @return группа студента.
     */
    @NonNull
    public String group() {
        return mStudentGroup;
    }

    /**
     * @return семестры студента.
     */
    @NonNull
    public List<String> semesters() {
        return mSemesters;
    }

    /**
     * @return время получения информации.
     */
    @NonNull
    public Calendar time() {
        return mTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentData data = (StudentData) o;
        return mStudentName.equals(data.mStudentName) &&
                mStudentGroup.equals(data.mStudentGroup) &&
                mSemesters.equals(data.mSemesters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mStudentName, mStudentGroup, mSemesters);
    }

    /**
     * Сохраняет информацию о студенте в кэш.
     * @param data информация о студенте.
     * @param cacheDirectory директория с кэшом приложения.
     */
    public static void saveCacheData(@NonNull StudentData data, @Nullable File cacheDirectory) {
        if (cacheDirectory == null) {
            return;
        }

        File cacheFile = FileUtils.getFile(cacheDirectory,STUDENT_FOLDER, "student.json");
        String json = new Gson().toJson(data);

        try {
            FileUtils.writeStringToFile(cacheFile, json, StandardCharsets.UTF_8, false);
        } catch (IOException ignored) {

        }
    }

    /**
     * Загружает информацию о студенте из кэша.
     * @param cacheDirectory директория с кэшом приложения.
     * @return информация о студенте.
     */
    @Nullable
    public static StudentData loadCacheData(@Nullable File cacheDirectory) {
        if (cacheDirectory == null) {
            return null;
        }

        File cacheFile = FileUtils.getFile(cacheDirectory, STUDENT_FOLDER, "student.json");
        if (!cacheFile.exists()) {
            return null;
        }

        try {
            String json = FileUtils.readFileToString(cacheFile, StandardCharsets.UTF_8);
            return new Gson().fromJson(json, StudentData.class);
        } catch (IOException | JsonSyntaxException ignored) {

        }

        return null;
    }

    /**
     * Удаляет за кэшированные данные.
     * @param cacheDirectory директория с кэшом приложения.
     */
    public static void clearCacheData(@Nullable File cacheDirectory) {
        if (cacheDirectory == null) {
            return;
        }
        File cacheDir = FileUtils.getFile(cacheDirectory, STUDENT_FOLDER);

        FileUtils.deleteQuietly(cacheDir);
    }
}
