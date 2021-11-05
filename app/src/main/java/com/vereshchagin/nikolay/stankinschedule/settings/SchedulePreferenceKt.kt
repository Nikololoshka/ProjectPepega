package com.vereshchagin.nikolay.stankinschedule.settings

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Класс-обертка для доступа к настройкам расписания.
 *
 * @param context контекст приложения.
 */
class SchedulePreferenceKt @Inject constructor(
    @ApplicationContext context: Context,
) {
    /**
     * Объект для хранения настроек расписания.
     */
    private val preference = context.getSharedPreferences(SCHEDULE_PREFERENCE, Context.MODE_PRIVATE)

    var favoriteScheduleId: Long
        get() = preference.getLong(FAVORITE_SCHEDULE_ID, -1)
        set(value) {
            preference.edit()
                .putLong(FAVORITE_SCHEDULE_ID, value)
                .apply()
        }

    var migrateToVersion2: Boolean
        get() = preference.getBoolean(MIGRATE_SCHEDULE, false)
        set(value) {
            preference.edit()
                .putBoolean(MIGRATE_SCHEDULE, value)
                .apply()
        }

    companion object {
        private const val SCHEDULE_PREFERENCE = "schedule_preference"

        private const val FAVORITE_SCHEDULE_ID = "favorite_schedule_id"
        private const val MIGRATE_SCHEDULE = "migrate_schedule"
    }
}