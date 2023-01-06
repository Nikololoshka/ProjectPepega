package com.vereshchagin.nikolay.stankinschedule.journal.core.data.repository

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.exceptions.StudentAuthorizedException
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.StudentCredentials
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalSecureRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class JournalSecureRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : JournalSecureRepository {

    // Кэш данных для учетной записи
    private var cachedCredentials: StudentCredentials? = null

    private val masterKey
        get() = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

    private val preferences
        get() = try {
            EncryptedSharedPreferences.create(
                context,
                MODULE_JOURNAL_SECURE_PREFERENCE,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            throw StudentAuthorizedException(e)
        }


    @kotlin.jvm.Throws(StudentAuthorizedException::class)
    override suspend fun signIn(credentials: StudentCredentials) {
        try {
            preferences.edit {
                putString(LOGIN, credentials.login)
                putString(PASSWORD, credentials.password)
            }
        } catch (e: StudentAuthorizedException) {
            // Попытка убрать preference, т.к. раньше не было
            // добавлено исключение на backup
            tryClearPreference()

            preferences.edit {
                putString(LOGIN, credentials.login)
                putString(PASSWORD, credentials.password)
            }
        }
    }

    private fun tryClearPreference() {
        context.getSharedPreferences(MODULE_JOURNAL_SECURE_PREFERENCE, Context.MODE_PRIVATE).edit {
            clear()
        }
        Log.d("JournalSecureRepositoryImpl", "signIn: try clear...")
    }

    @kotlin.jvm.Throws(StudentAuthorizedException::class)
    override suspend fun signOut() {
        try {
            preferences.edit {
                clear()
            }
        } catch (ignored: Exception) {
            tryClearPreference()
        }
    }

    @kotlin.jvm.Throws(StudentAuthorizedException::class)
    override suspend fun signCredentials(): StudentCredentials {
        val cache = cachedCredentials
        if (cache != null) return cache

        val login = preferences.getString(LOGIN, null)
        val password = preferences.getString(PASSWORD, null)

        if (login == null || password == null) {
            throw StudentAuthorizedException("Credentials is null")
        }

        val credentials = StudentCredentials(login, password)
        cachedCredentials = credentials

        return credentials
    }

    companion object {
        /**
         * Необходимо прописать исключения для правил backup!!!
         */
        private const val MODULE_JOURNAL_SECURE_PREFERENCE = "module_journal_secure_preference"
        private const val LOGIN = "login"
        private const val PASSWORD = "password"
    }
}