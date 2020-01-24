package com.github.nikololoshka.pepegaschedule.modulejournal.view;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.loader.content.AsyncTaskLoader;

import com.github.nikololoshka.pepegaschedule.modulejournal.network.ModuleJournalService;
import com.github.nikololoshka.pepegaschedule.modulejournal.network.SemestersResponse;
import com.github.nikololoshka.pepegaschedule.modulejournal.view.data.StudentData;
import com.github.nikololoshka.pepegaschedule.settings.ModuleJournalPreference;

import java.io.IOException;
import java.security.GeneralSecurityException;

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
            if (cacheData != null && (cacheData.time() - System.currentTimeMillis()) < 1000 * 60 * 60) {
                data.response = cacheData;
                data.login = login;
                data.password = password;
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
            }

            // TODO: Обработка ошибок

        // java.net.SocketTimeoutException: failed to connect to lk.stankin.ru/109.120.166.10 (port 443) after 10000ms
        // java.net.UnknownHostException: Unable to resolve host "lk.stankin.ru": No address associated with hostname
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    class LoadData {
        @Nullable
        SemestersResponse response;
        @Nullable
        String login;
        @Nullable
        String password;
    }
}
