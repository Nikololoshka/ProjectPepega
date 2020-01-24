package com.github.nikololoshka.pepegaschedule.modulejournal.view.paging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.nikololoshka.pepegaschedule.modulejournal.network.MarkResponse;
import com.github.nikololoshka.pepegaschedule.modulejournal.network.ModuleJournalService;
import com.github.nikololoshka.pepegaschedule.modulejournal.view.data.SemestersMarks;
import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Response;

/**
 * Хранилище оценок. Получает оценки из кэша или по сети.
 */
public class SemestersStorage {

    private static final String TAG = "SemestersStorageLog";

    private static final String SEMESTERS_FOLDER = "semesters_data";

    @Nullable
    private ArrayList<String> mSemesters;;
    @Nullable
    private String mLogin;
    @Nullable
    private String mPassword;
    @Nullable
    private File mCacheDirectory;

    public SemestersStorage() {
        mSemesters = null;
        mLogin = null;
        mPassword = null;
    }

    /** Устанавливает семестры студента.
     * @param semesters список семестров.
     */
    public void setSemesters(@Nullable ArrayList<String> semesters) {
        mSemesters = semesters;
    }

    /**
     * Устанавливает логин для доступа к загрузки данных с сервера.
     * @param login логин.
     */
    public void setLogin(@Nullable String login) {
        mLogin = login;
    }

    /**
     * Устанавливает пароль для доступа к загрузки данных с сервера.
     * @param password пароль.
     */
    public void setPassword(@Nullable String password) {
        mPassword = password;
    }

    /**
     * Устанавливет директорию с кэшом приложения.
     * @param cacheDirectory директория кэша.
     */
    public void setCacheDirectory(@Nullable File cacheDirectory) {
        mCacheDirectory = cacheDirectory;
    }

    /**
     * Возвращает количетво семестров.
     * @return количетво семестров.
     */
    int semestersCount() {
        if (mSemesters == null) {
            return 0;
        }

        return mSemesters.size();
    }

    /**
     * Возвращает имя семестра по его индексу.
     * @param index индекс.
     * @return название семестра.
     */
    @NonNull
    public String semesterTitle(int index) {
        if (mSemesters == null || index < 0 || index >= mSemesters.size()) {
            return "";
        }
        return mSemesters.get(index);
    }

    /**
     * Загружает данные для отображения из кэша или получает от сервера.
     * @param loadPosition позиция (номер семестра) загружаемых данных.
     * @return данные с оценками.
     */
    @NonNull
    List<SemestersMarks> loadData(int loadPosition) {
        if (mSemesters == null || mLogin == null || mPassword == null
                || loadPosition < 0 || loadPosition >= mSemesters.size()) {
            return Collections.singletonList(new SemestersMarks());
        }

        String semester = mSemesters.get(loadPosition);

        SemestersMarks marks;
        try {
            marks = loadCacheData(semester);
            // загружены из кэша и менее чем 10 минут назад
            if (marks != null && (System.currentTimeMillis() - marks.time()) < 1000 * 60 * 10) {
                return Collections.singletonList(marks);
            }

            Response<List<MarkResponse>> response = ModuleJournalService.getInstance()
                    .api2()
                    .getMarks(mLogin, mPassword, semester)
                    .execute();

            marks = SemestersMarks.fromResponse(response.body());
            saveCacheData(marks, semester);

            // кэшируем данные для отображения
            marks.createCellsData();
            marks.createColumnsData();
            marks.createRowsData();

        } catch (IOException e) {
            e.printStackTrace();
            marks = new SemestersMarks();
        }

        return Collections.singletonList(marks);
    }

    /**
     * Загружает оценки из кэша.
     * @param semester название семестра, которые необхожимо загрузить.
     * @return Оценки за семестр из кэша.
     */
    @Nullable
    private SemestersMarks loadCacheData(@NonNull String semester) {
        if (mCacheDirectory == null) {
            return null;
        }

        File cacheFile = FileUtils.getFile(mCacheDirectory,SEMESTERS_FOLDER, semester + ".json");
        if (!cacheFile.exists()) {
            return null;
        }

        try {
            String json = FileUtils.readFileToString(cacheFile, StandardCharsets.UTF_8);
            return new Gson().fromJson(json, SemestersMarks.class);
        } catch (IOException ignored) {

        }

        return null;
    }

    /**
     * Сохраняет оценки в кэш.
     * @param marks оценки за семестр.
     * @param semester название семестра.
     */
    private void saveCacheData(@NonNull SemestersMarks marks, @NonNull String semester) {
        if (mCacheDirectory == null) {
            return;
        }

        File cacheFile = FileUtils.getFile(mCacheDirectory,SEMESTERS_FOLDER, semester + ".json");
        String json = new Gson().toJson(marks);

        try {
            FileUtils.writeStringToFile(cacheFile, json, StandardCharsets.UTF_8, false);
        } catch (IOException ignored) {

        }
    }
}
