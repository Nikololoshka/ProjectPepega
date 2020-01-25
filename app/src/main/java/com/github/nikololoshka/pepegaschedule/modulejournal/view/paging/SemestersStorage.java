package com.github.nikololoshka.pepegaschedule.modulejournal.view.paging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.nikololoshka.pepegaschedule.MainApplication;
import com.github.nikololoshka.pepegaschedule.modulejournal.network.MarkResponse;
import com.github.nikololoshka.pepegaschedule.modulejournal.network.ModuleJournalErrorUtils;
import com.github.nikololoshka.pepegaschedule.modulejournal.network.ModuleJournalService;
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
            marks = SemestersMarks.loadCacheData(semester, mCacheDirectory);
            // загружены из кэша и менее чем 10 минут назад
            if (marks != null && isOverData(marks.time(), 1000 * 60 * 10)) {
                // кэшируем данные для отображения
                marks.createCellsData();
                marks.createColumnsData();
                marks.createRowsData();

                return Collections.singletonList(marks);
            }

            Response<List<MarkResponse>> response = ModuleJournalService.getInstance()
                    .api2()
                    .getMarks(mLogin, mPassword, semester)
                    .execute();

            if (response.isSuccessful()) {
                marks = SemestersMarks.fromResponse(response.body());
                SemestersMarks.saveCacheData(marks, semester, mCacheDirectory);
            } else {
                marks = new SemestersMarks();
                // TODO: 25/01/20 Используется MainApplication как Context
                marks.setError(ModuleJournalErrorUtils.responseError(response, MainApplication.getInstance()));
            }

            // кэшируем данные для отображения
            marks.createCellsData();
            marks.createColumnsData();
            marks.createRowsData();

        } catch (IOException e) {
            e.printStackTrace();
            marks = new SemestersMarks();
            // TODO: 25/01/20 Используется MainApplication как Context
            marks.setError(ModuleJournalErrorUtils.exceptionError(e, MainApplication.getInstance()));
        }

        return Collections.singletonList(marks);
    }

    /**
     * Проверяет, истек ли срок хранения кэша.
     * @param calendar время загрузки даных в кэше.
     * @param delta времяхранения.
     * @return true - время истекло, иначе false.
     */
    private boolean isOverData(@NonNull Calendar calendar, long delta) {
        Calendar today = new GregorianCalendar();
        return (today.getTimeInMillis() - calendar.getTimeInMillis()) < delta;
    }
}
