package com.vereshchagin.nikolay.stankinschedule.settings;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Класс-обертка для доступа к настройкам модульного журнала.
 */
public class ModuleJournalPreference {

    private static final String MODULE_JOURNAL_PREFERENCE = "module_journal_preference";
    private static final String MODULE_JOURNAL_SECURE_PREFERENCE = "module_journal_secure_preference";

    private static final String SIGN_IN = "sign_in";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";

    /**
     * Устанавливает, выполнен ли вход/выход в модульной журнал.
     * Если выход, то удаляет все предыдущие сохраненные данные.
     * @param context контекст.
     * @param sign true - вход, false - выход.
     */
    public static void setSignIn(@NonNull Context context, boolean sign) {
        SharedPreferences preferences =
                context.getSharedPreferences(MODULE_JOURNAL_PREFERENCE, Context.MODE_PRIVATE);

        preferences.edit()
                .putBoolean(SIGN_IN, sign)
                .apply();

        if (!sign) {
            try {
                saveSignData(context, "", "");
            } catch (GeneralSecurityException | IOException ignored) {

            }
        }
    }

    /**
     * Был ли выполнен вход в модульный журнал.
     * @param context контекст.
     * @return true - вход выполнен, иначе false.
     */
    public static boolean signIn(@NonNull Context context) {
        return context.getSharedPreferences(MODULE_JOURNAL_PREFERENCE, Context.MODE_PRIVATE)
                .getBoolean(SIGN_IN, false);
    }

    /**
     * Сохраняет данные для авторизации.
     * @param context котекст.
     * @param login логин.
     * @param password пароль.
     * @throws GeneralSecurityException ошибка доступа.
     * @throws IOException невозможно записать/прочесть файл.
     */
    public static void saveSignData(@NonNull Context context, @NonNull String login,
                                    @NonNull String password) throws GeneralSecurityException, IOException {

        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

        SharedPreferences preferences = EncryptedSharedPreferences.create(
                MODULE_JOURNAL_SECURE_PREFERENCE,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );


        preferences.edit()
                .putString(LOGIN, login)
                .putString(PASSWORD, password)
                .apply();
    }

    /**
     * Загружает данные для авторизации.
     * @param context контекст.
     * @return пара значений: логин и пароль.
     * @throws GeneralSecurityException ошибка доступа.
     * @throws IOException невозможно записать/прочесть файл.
     */
    public static Pair< String, String> loadSignData(@NonNull Context context)
            throws GeneralSecurityException, IOException {

        String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

        SharedPreferences preferences = EncryptedSharedPreferences.create(
                MODULE_JOURNAL_SECURE_PREFERENCE,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );

        return new Pair<>(preferences.getString(LOGIN, ""),
                preferences.getString(PASSWORD, ""));
    }
}
