package com.vereshchagin.nikolay.stankinschedule.settings.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.model.PairColorType
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.repository.SchedulePreference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val SCHEDULE_PREFERENCE = "schedule_preference"
private val Context.dataStore by preferencesDataStore(name = SCHEDULE_PREFERENCE)

/**
 * Класс-обертка для доступа к настройкам расписания.
 */
class ScheduleDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) : SchedulePreference {

    override fun favorite(): Flow<Long> = context.dataStore.data
        .map { preferences -> preferences[FAVORITE_SCHEDULE_ID] ?: -1 }

    override suspend fun setFavorite(id: Long) {
        context.dataStore.edit { preferences ->
            val lastId = preferences[FAVORITE_SCHEDULE_ID]
            preferences[FAVORITE_SCHEDULE_ID] = if (lastId != id) id else -1
        }
    }

    override fun isVerticalViewer(): Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[VERTIVAL_VIEWER] ?: false }

    override suspend fun setVerticalViewer(isVertical: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[VERTIVAL_VIEWER] = isVertical
        }
    }

    override fun scheduleColor(type: PairColorType): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[keyForColorType(type)] ?: type.hex
        }
    }

    override fun scheduleColorGroup(): Flow<PairColorGroup> {
        return context.dataStore.data.map { preferences ->
            PairColorGroup(
                lectureColor = preferences.colorOrDefault(PairColorType.Lecture),
                seminarColor = preferences.colorOrDefault(PairColorType.Seminar),
                laboratoryColor = preferences.colorOrDefault(PairColorType.Laboratory),
                subgroupAColor = preferences.colorOrDefault(PairColorType.SubgroupA),
                subgroupBColor = preferences.colorOrDefault(PairColorType.SubgroupB),
            )
        }
    }

    override suspend fun setScheduleColor(hex: String, type: PairColorType) {
        context.dataStore.edit { preferences -> preferences[keyForColorType(type)] = hex }
    }

    private fun androidx.datastore.preferences.core.Preferences.colorOrDefault(type: PairColorType): String {
        return this[keyForColorType(type)] ?: type.hex
    }

    companion object {

        private fun keyForColorType(type: PairColorType): androidx.datastore.preferences.core.Preferences.Key<String> {
            return when (type) {
                PairColorType.Lecture -> androidx.datastore.preferences.core.stringPreferencesKey("schedule_lecture_color")
                PairColorType.Seminar -> androidx.datastore.preferences.core.stringPreferencesKey("schedule_seminar_color")
                PairColorType.Laboratory -> androidx.datastore.preferences.core.stringPreferencesKey(
                    "schedule_laboratory_color"
                )
                PairColorType.SubgroupA -> androidx.datastore.preferences.core.stringPreferencesKey(
                    "schedule_subgroup_a_color"
                )
                PairColorType.SubgroupB -> androidx.datastore.preferences.core.stringPreferencesKey(
                    "schedule_subgroup_b_color"
                )
            }
        }

        private val FAVORITE_SCHEDULE_ID
            get() = androidx.datastore.preferences.core.longPreferencesKey(
                "favorite_schedule_id"
            )

        private val VERTIVAL_VIEWER
            get() = androidx.datastore.preferences.core.booleanPreferencesKey(
                "schedule_vertical_viewer"
            )
    }
}