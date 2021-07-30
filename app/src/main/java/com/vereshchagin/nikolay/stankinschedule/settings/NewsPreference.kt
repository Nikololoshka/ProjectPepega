package com.vereshchagin.nikolay.stankinschedule.settings

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.joda.time.DateTime
import javax.inject.Inject

/**
 * Класс-обертка для доступа к настройкам новостей.
 *
 * @param context контекст приложения.
 */
class NewsPreference @Inject constructor(
    @ApplicationContext context: Context
) {
    /**
     * Объект для хранения настроек новостей.
     */
    private val preference = context.getSharedPreferences(NEWS_PREFERENCE, Context.MODE_PRIVATE)

    /**
     * Возвращает время последнего обновления новостей для источника.
     * Если такого нет, то возвращается null.
     *
     * @param newsSubdivision номер подразделения новостей.
     */
    fun lastNewsUpdate(newsSubdivision: Int): DateTime? {
        return try {
            val date = preference.getString("${NEWS_DATE}_$newsSubdivision", null)
            DateTime.parse(date)
        } catch (ignored: Exception) {
            return null
        }
    }

    /**
     * Устанавливает последнюю дату обновления новостей для источника.
     *
     * @param newsSubdivision номер подразделения новостей.
     * @param date дата и время обновления.
     */
    fun setNewsUpdate(newsSubdivision: Int, date: DateTime = DateTime.now()) {
        preference.edit()
            .putString("${NEWS_DATE}_$newsSubdivision", date.toString())
            .apply()
    }

    companion object {
        /**
         * Название настроек новостей.
         */
        private const val NEWS_PREFERENCE = "news_preference"

        /**
         * Настройка с датой обновления новостей.
         */
        private const val NEWS_DATE = "news_date"
    }
}