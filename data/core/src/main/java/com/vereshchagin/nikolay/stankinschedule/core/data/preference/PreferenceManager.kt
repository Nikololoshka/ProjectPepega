package com.vereshchagin.nikolay.stankinschedule.core.data.preference

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.vereshchagin.nikolay.stankinschedule.core.domain.settings.PreferenceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import org.joda.time.DateTime
import javax.inject.Inject

class PreferenceManager @Inject constructor(
    @ApplicationContext context: Context,
) : PreferenceRepository {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    override fun getBoolean(key: String, default: Boolean): Boolean {
        return preferences.getBoolean(key, default)
    }

    override fun saveBoolean(key: String, value: Boolean) {
        preferences.edit { putBoolean(key, value) }
    }

    override fun getString(key: String): String? {
        return preferences.getString(key, null)
    }

    override fun saveString(key: String, value: String) {
        preferences.edit { putString(key, value) }
    }

    override fun getDateTime(key: String): DateTime? {
        val dateString = preferences.getString(key, null) ?: return null
        return DateTime.parse(dateString)
    }

    override fun saveDateTime(key: String, value: DateTime) {
        preferences.edit { putString(key, value.toString()) }
    }
}