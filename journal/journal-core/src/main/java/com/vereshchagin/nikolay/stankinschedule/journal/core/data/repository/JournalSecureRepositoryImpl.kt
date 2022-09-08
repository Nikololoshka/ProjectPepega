package com.vereshchagin.nikolay.stankinschedule.journal.core.data.repository

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.model.StudentCredentials
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalSecureRepository
import com.vereshchagin.nikolay.stankinschedule.journal.core.utils.StudentAuthorizedException
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class JournalSecureRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : JournalSecureRepository {

    // Кэш данных для учетной записи
    private var cachedCredentials: StudentCredentials? = null

    // TODO("Возможно исключение при создании preferences")
    private val masterKey
        get() = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

    /**
     * java.io.FileNotFoundException: can't read keyset; the pref value __androidx_security_crypto_encrypted_prefs_key_keyset__ does not exist
     */
    private val preferences
        get() = EncryptedSharedPreferences.create(
            context,
            MODULE_JOURNAL_SECURE_PREFERENCE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    override suspend fun signIn(credentials: StudentCredentials) {
        preferences.edit {
            putString(LOGIN, credentials.login)
            putString(PASSWORD, credentials.password)
        }
    }

    override suspend fun signOut() {
        preferences.edit {
            clear()
        }
    }

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
        private const val MODULE_JOURNAL_SECURE_PREFERENCE = "module_journal_secure_preference"
        private const val LOGIN = "login"
        private const val PASSWORD = "password"
    }
}