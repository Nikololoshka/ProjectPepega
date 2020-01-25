package com.github.nikololoshka.pepegaschedule.modulejournal.view;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.loader.content.AsyncTaskLoader;

import com.github.nikololoshka.pepegaschedule.modulejournal.network.ModuleJournalError;
import com.github.nikololoshka.pepegaschedule.modulejournal.network.ModuleJournalErrorUtils;
import com.github.nikololoshka.pepegaschedule.modulejournal.network.ModuleJournalService;
import com.github.nikololoshka.pepegaschedule.modulejournal.network.SemestersResponse;
import com.github.nikololoshka.pepegaschedule.modulejournal.view.model.StudentData;
import com.github.nikololoshka.pepegaschedule.settings.ModuleJournalPreference;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import retrofit2.Response;

/**
 * Загрузчик модульного журнала.
 */
public class ModuleJournalLoader extends AsyncTaskLoader<ModuleJournalLoader.LoadData> {

    ModuleJournalLoader(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    public LoadData loadInBackground() {
        LoadData data = new LoadData();

        try {
            Pair<String, String> authorization = ModuleJournalPreference.loadSignData(getContext());
            String login = authorization.first == null ? "" : authorization.first;
            String password = authorization.second == null ? "" : authorization.second;

            SemestersResponse cacheData = StudentData.loadCacheData(getContext().getCacheDir());
            data.response = cacheData;
            data.login = login;
            data.password = password;

            if (cacheData != null && isOverData(cacheData.time(), 1000 * 60 * 60)) {
                return data;
            }

            Response<SemestersResponse> response = ModuleJournalService.getInstance()
                    .api2()
                    .getSemesters(login, password)
                    .execute();

            if (response.isSuccessful()) {
                if (response.body() != null) {
                    StudentData.saveCacheData(response.body(), getContext().getCacheDir());
                }

                data.response = response.body();
                data.login = login;
                data.password = password;
                return data;
            } else {
                data.error = ModuleJournalErrorUtils.responseError(response, getContext());
            }

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            data.error = ModuleJournalErrorUtils.exceptionError(e, getContext());
        }

        return data;
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

    class LoadData {
        @Nullable
        SemestersResponse response;
        @Nullable
        String login;
        @Nullable
        String password;

        @Nullable
        ModuleJournalError error;
    }
}
