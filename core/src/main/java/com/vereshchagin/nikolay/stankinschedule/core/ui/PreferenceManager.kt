package com.vereshchagin.nikolay.stankinschedule.core.ui

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import org.joda.time.DateTime
import javax.inject.Inject

class PreferenceManager @Inject constructor(
    @ApplicationContext context: Context,
) {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun getString(key: String): String? {
        return preferences.getString(key, null)
    }

    fun saveString(key: String, value: String) {
        preferences.edit { putString(key, value) }
    }

    fun getDateTime(key: String): DateTime? {
        val dateString = preferences.getString(key, null) ?: return null
        return DateTime.parse(dateString)
    }

    fun saveDateTime(key: String, value: DateTime) {
        preferences.edit { putString(key, value.toString()) }
    }
}