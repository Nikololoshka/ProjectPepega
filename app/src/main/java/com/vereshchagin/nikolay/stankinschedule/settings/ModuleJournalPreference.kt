package com.vereshchagin.nikolay.stankinschedule.settings

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.io.IOException
import java.security.GeneralSecurityException

/**
 * Класс-обертка для доступа к доступу к личным настройкам модульного журнала.
 */
object ModuleJournalPreference {

    private const val MODULE_JOURNAL_PREFERENCE = "module_journal_preference"
    private const val MODULE_JOURNAL_SECURE_PREFERENCE = "module_journal_secure_preference"
    private const val SIGN_IN = "sign_in"
    private const val LOGIN = "login"
    private const val PASSWORD = "password"

    /**
     * Выполняет вход в модульный журнал.
     * Сохраняет логин и пароль для входа в зашифрованное хранилище.
     *
     * @param context контекст приложения.
     * @param login логин для входа
     * @param password пароль для входа
     * @throws GeneralSecurityException ошибка доступа.
     * @throws IOException невозможно записать/прочесть файл.
     */
    @JvmStatic
    fun signIn(context: Context, login: String, password: String) {
        saveSignData(context, login, password)
        context.getSharedPreferences(MODULE_JOURNAL_PREFERENCE, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(SIGN_IN, true)
            .apply()
    }

    /**
     * Выполняет выход из модульного журнала.
     * Все сохраненные данные удаляются.
     *
     * @param context  контекст приложения.
     * @throws GeneralSecurityException ошибка доступа.
     * @throws IOException невозможно записать/прочесть файл.
     */
    @JvmStatic
    fun signOut(context: Context) {
        saveSignData(context, "", "")
        context.getSharedPreferences(MODULE_JOURNAL_PREFERENCE, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(SIGN_IN, false)
            .apply()
    }

    /**
     * Был ли выполнен вход в модульный журнал.
     * @param context контекст приложения.
     * @return true - вход выполнен, иначе false.
     */
    @JvmStatic
    fun isSignIn(context: Context): Boolean {
        return context.getSharedPreferences(MODULE_JOURNAL_PREFERENCE, Context.MODE_PRIVATE)
            .getBoolean(SIGN_IN, false)
    }

    /**
     * Загружает данные для авторизации.
     * @param context контекст приложения.
     * @return пара значений: логин и пароль.
     * @throws GeneralSecurityException ошибка доступа.
     * @throws IOException невозможно записать/прочесть файл.
     */
    @JvmStatic
    fun signInData(context: Context): Pair<String, String> {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val preferences = EncryptedSharedPreferences.create(
            context,
            MODULE_JOURNAL_SECURE_PREFERENCE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val login = preferences.getString(LOGIN, "")!!
        val password = preferences.getString(PASSWORD, "")!!

        return login to password
    }

    /**
     * Сохраняет данные для авторизации.
     * @param context контекст приложения.
     * @param login логин.
     * @param password пароль.
     * @throws GeneralSecurityException ошибка доступа.
     * @throws IOException невозможно записать/прочесть файл.
     */
    @JvmStatic
    private fun saveSignData(
        context: Context,
        login: String,
        password: String
    ) {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val preferences = EncryptedSharedPreferences.create(
            context,
            MODULE_JOURNAL_SECURE_PREFERENCE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        preferences.edit()
            .putString(LOGIN, login)
            .putString(PASSWORD, password)
            .apply()
    }
}