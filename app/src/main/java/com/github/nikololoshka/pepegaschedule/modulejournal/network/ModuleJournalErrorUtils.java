package com.github.nikololoshka.pepegaschedule.modulejournal.network;

import android.content.Context;

import androidx.annotation.NonNull;

import com.github.nikololoshka.pepegaschedule.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Response;

/**
 * Обработчик ошибок при работе с сетью.
 */
public class ModuleJournalErrorUtils {

    /**
     * Достает информацию об ошибке по полученному ответу.
     * @param response ответ от сервера.
     * @param context контекст.
     * @return информация об ошибке.
     */
    public static ModuleJournalError responseError(@NonNull Response<?> response, @NonNull Context context) {
        ModuleJournalError data = new ModuleJournalError();
        data.setErrorCode(response.code());

        switch (response.code()) {
            // ошибка авторизации
            case HttpURLConnection.HTTP_UNAUTHORIZED: {
                data.setErrorTitle(context.getString(R.string.mj_failed_unauthorized));
                data.setErrorDescription(context.getString(R.string.mj_failed_unauthorized_description));
            }
            default: {
                data.setErrorTitle(context.getString(R.string.mj_data_problem));
                data.setErrorDescription(response.message());
            }
        }

        return data;
    }

    /**
     * Достает информацию об ошибке по полученному исключению во время работы.
     * @param e исключение.
     * @param context контекст.
     * @return информация об ошибке.
     */
    public static ModuleJournalError exceptionError(@NonNull IOException e, @NonNull Context context) {
        ModuleJournalError data = new ModuleJournalError();
        data.setErrorCode(-1);

        // не удалось подключится к "lk.stankin.ru"
        if (e instanceof UnknownHostException) {
            data.setErrorTitle(context.getString(R.string.mj_unknown_host));
            data.setErrorDescription(context.getString(R.string.mj_unknown_host_description));
            return data;
        }
        // время подключения истекло
        if (e instanceof SocketTimeoutException) {
            data.setErrorTitle(context.getString(R.string.mj_socket_timeout));
            data.setErrorDescription(context.getString(R.string.mj_socket_timeout_description));
            return data;
        }

        data.setErrorTitle(context.getString(R.string.mj_data_problem));

        String description = e.getLocalizedMessage();
        data.setErrorDescription(description == null ? e.toString() : description);

        return data;
    }

}
