package com.github.nikololoshka.pepegaschedule.modulejournal;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.github.nikololoshka.pepegaschedule.R;
import com.github.nikololoshka.pepegaschedule.modulejournal.connection.ModuleJournalConnection;
import com.github.nikololoshka.pepegaschedule.modulejournal.connection.ModuleJournalConnectionException;
import com.github.nikololoshka.pepegaschedule.modulejournal.connection.ModuleJournalJsonParser;
import com.github.nikololoshka.pepegaschedule.modulejournal.model.StudentData;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Загрузчик для авторизации на модульном журнале.
 */
public class ModuleJournalLoginLoader extends AsyncTaskLoader<ModuleJournalLoginLoader.LoadData> {

    /**
     * Логин.
     */
    @Nullable
    private String mLogin;
    /**
     * Пароль.
     */
    @Nullable
    private String mPassword;

    ModuleJournalLoginLoader(@NonNull Context context) {
        super(context);
    }

    /**
     * Выполянят вход в модульный журнал.
     * @param login логин.
     * @param password пароль.
     */
    void signIn(@Nullable String login, @Nullable String password) {
        mLogin = login;
        mPassword = password;

        forceLoad();
    }

    @Nullable
    @Override
    public LoadData loadInBackground() {
        LoadData data = new LoadData();

//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//        }

        if (mLogin == null || mPassword == null) {
            data.errorTitle = "Logistical error";
            data.errorDescription = "Incorrect authorization data";
            return data;
        }

        try {
            ModuleJournalConnection mjConnection = new ModuleJournalConnection();
            String jsonResponse = mjConnection.requestSemesters(mLogin, mPassword);
            data.studentData = ModuleJournalJsonParser.parseSemesters(jsonResponse);
            data.signIn = true;
        } catch (ModuleJournalConnectionException e) {
            // ошибки при работе с модульном журналом

            data.errorTitle = e.message();

            switch (e.code()) {
                case HttpURLConnection.HTTP_UNAUTHORIZED: {
                    data.errorDescription = getContext().getString(R.string.mj_failed_unauthorized);
                    break;
                }
                case HttpURLConnection.HTTP_GATEWAY_TIMEOUT: {
                    data.errorDescription = getContext().getString(R.string.mj_gateway_timeout);
                    break;
                }
            }

        } catch (JSONException e) {
            // ошибка парсинга ответа
            data.errorTitle = getContext().getString(R.string.mj_read_data_error);
            data.errorDescription = getContext().getString(R.string.mj_read_data_error_description);
            data.errorLog = e.getMessage();

            e.printStackTrace();
        } catch (IOException e) {
            // ошибки при работе с данными сети
            data.errorTitle = getContext().getString(R.string.mj_data_error);
            data.errorDescription = getContext().getString(R.string.mj_data_error_description);
            data.errorLog = e.getMessage();

            e.printStackTrace();
        }

        return data;
    }

    /**
     * Результа работы загрузчика.
     */
    class LoadData {
        @NonNull
        String errorTitle = "";
        @NonNull
        String errorDescription = "";
        @Nullable
        String errorLog = null;
        @Nullable
        StudentData studentData = null;

        boolean signIn = false;
    }
}
