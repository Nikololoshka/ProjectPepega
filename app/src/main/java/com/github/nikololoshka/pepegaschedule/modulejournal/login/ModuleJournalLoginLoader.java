package com.github.nikololoshka.pepegaschedule.modulejournal.login;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.github.nikololoshka.pepegaschedule.modulejournal.network.ModuleJournalError;
import com.github.nikololoshka.pepegaschedule.modulejournal.network.ModuleJournalErrorUtils;
import com.github.nikololoshka.pepegaschedule.modulejournal.network.ModuleJournalService;
import com.github.nikololoshka.pepegaschedule.modulejournal.network.SemestersResponse;
import com.github.nikololoshka.pepegaschedule.modulejournal.view.model.StudentData;
import com.github.nikololoshka.pepegaschedule.settings.ModuleJournalPreference;

import java.io.IOException;
import java.security.GeneralSecurityException;

import retrofit2.Response;

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

        if (mLogin == null || mPassword == null) {
            data.errorTitle = "Logistical error";
            data.errorDescription = "Incorrect authorization data";
            return data;
        }

        try {
            Response<SemestersResponse> response = ModuleJournalService.getInstance()
                    .api2()
                    .getSemesters(mLogin, mPassword)
                    .execute();

            if (response.isSuccessful()) {
                ModuleJournalPreference.saveSignData(getContext(), mLogin, mPassword);
                ModuleJournalPreference.setSignIn(getContext(), true);
                data.signIn = true;

                if (response.body() != null) {
                    StudentData.saveCacheData(response.body(), getContext().getCacheDir());
                }
            } else {
                data.error = ModuleJournalErrorUtils.responseError(response,getContext());
            }
        } catch (IOException e) {
            e.printStackTrace();
            data.error = ModuleJournalErrorUtils.exceptionError(e, getContext());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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
        ModuleJournalError error;

        boolean signIn = false;
    }
}
