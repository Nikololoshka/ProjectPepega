package com.github.nikololoshka.pepegaschedule.modulejournal.view.paging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.nikololoshka.pepegaschedule.modulejournal.network.ModuleJournalErrorUtils;
import com.github.nikololoshka.pepegaschedule.modulejournal.network.ModuleJournalService;
import com.github.nikololoshka.pepegaschedule.modulejournal.network.response.MarkResponse;
import com.github.nikololoshka.pepegaschedule.modulejournal.view.model.SemestersMarks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Response;

/**
 * Хранилище оценок. Получает оценки из кэша или по сети.
 */
public class SemestersStorage {

    private static final String TAG = "SemestersStorageLog";

    /**
     * Список семестров.
     */
    @Nullable
    private List<String> mSemesters;
    /**
     * Логин для входа.
     */
    @Nullable
    private String mLogin;
    /**
     * Пароль для входа.
     */
    @Nullable
    private String mPassword;
    /**
     * Директория с кэшом.
     */
    @Nullable
    private File mCacheDirectory;

    /**
     * Использовать ли кэш при загрузке данных.
     */
    @NonNull
    private ArrayList<Boolean> mUseCache;

    public SemestersStorage() {
        mSemesters = null;
        mLogin = null;
        mPassword = null;

        mUseCache = new ArrayList<>();
    }

    /** Устанавливает семестры студента.
     * @param semesters список семестров.
     */
    public void setSemesters(@Nullable List<String> semesters) {
        mSemesters = semesters;

        if (mSemesters != null) {
            for (int i = 0; i < mSemesters.size(); i++) {
                mUseCache.add(true);
            }
        }
    }

    public void setUseCache(boolean useCache) {
        Collections.fill(mUseCache, useCache);
    }

    /**
     * Устанавливает логин для доступа к загрузке данных с сервера.
     * @param login логин.
     */
    public void setLogin(@Nullable String login) {
        mLogin = login;
    }

    /**
     * Устанавливает пароль для доступа к загрузке данных с сервера.
     * @param password пароль.
     */
    public void setPassword(@Nullable String password) {
        mPassword = password;
    }

    /**
     * Устанавливает директорию с кэшом приложения.
     * @param cacheDirectory директория кэша.
     */
    public void setCacheDirectory(@Nullable File cacheDirectory) {
        mCacheDirectory = cacheDirectory;
    }

    /**
     * Возвращает количество семестров.
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
        Boolean useCache = mUseCache.get(loadPosition);

        @Nullable
        SemestersMarks marks = useCache ? SemestersMarks.loadCacheData(semester, mCacheDirectory) : null;
        mUseCache.set(loadPosition, true);

        // загружены из кэша и менее чем 10 минут назад
        if (marks != null && isOverData(marks.time())) {
            // кэшируем данные для отображения
            marks.createCellsData();
            marks.createColumnsData();
            marks.createRowsData();

            return Collections.singletonList(marks);
        }

        try {
            // задержка между запросами
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {

            }

            Response<List<MarkResponse>> response = ModuleJournalService.getInstance()
                    .api2()
                    .getMarks(mLogin, mPassword, semester)
                    .execute();

            if (response.isSuccessful()) {
                marks = SemestersMarks.fromResponse(response.body());
                SemestersMarks.saveCacheData(marks, semester, mCacheDirectory);
            } else {
                if (marks == null) {
                    marks = new SemestersMarks();
                    marks.setError(ModuleJournalErrorUtils.responseError(response));

                    return Collections.singletonList(marks);
                } else {
                    marks.setCache(true);
                }
            }

            // кэшируем данные для отображения
            marks.createCellsData();
            marks.createColumnsData();
            marks.createRowsData();

        } catch (IOException e) {
            if (marks == null) {
                marks = new SemestersMarks();
                marks.setError(ModuleJournalErrorUtils.exceptionError(e));
            } else {
                marks.setCache(true);
            }
        }

        return Collections.singletonList(marks);
    }

    /**
     * Проверяет, истек ли срок хранения кэша.
     * @param calendar время загрузки данных в кэше.
     * @return true - время истекло, иначе false.
     */
    private boolean isOverData(@NonNull Calendar calendar) {
        Calendar today = new GregorianCalendar();
        // 600000L - 10 минут
        return (today.getTimeInMillis() - calendar.getTimeInMillis()) < 600000L;
    }
}
