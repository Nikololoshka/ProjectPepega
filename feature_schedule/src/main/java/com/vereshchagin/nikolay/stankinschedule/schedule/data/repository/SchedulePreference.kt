package com.vereshchagin.nikolay.stankinschedule.schedule.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


private const val SCHEDULE_PREFERENCE = "schedule_preference"
private val Context.dataStore by preferencesDataStore(name = SCHEDULE_PREFERENCE)

/**
 * Класс-обертка для доступа к настройкам расписания.
 */
class SchedulePreference @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun favorite(): Flow<Long> = context.dataStore.data
        .map { preferences -> preferences[FAVORITE_SCHEDULE_ID] ?: -1 }

    suspend fun setFavorite(id: Long) {
        context.dataStore.edit { preferences ->
            val lastId = preferences[FAVORITE_SCHEDULE_ID]
            preferences[FAVORITE_SCHEDULE_ID] = if (lastId != id) id else -1
        }
    }

    companion object {
        private val FAVORITE_SCHEDULE_ID = longPreferencesKey("favorite_schedule_id")
    }
}