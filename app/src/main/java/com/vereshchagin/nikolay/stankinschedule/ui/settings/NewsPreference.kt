package com.vereshchagin.nikolay.stankinschedule.ui.settings

import android.content.Context
import android.os.Build
import com.vereshchagin.nikolay.stankinschedule.utils.DateUtils
import com.vereshchagin.nikolay.stankinschedule.utils.DateUtils.Companion.formatDate
import com.vereshchagin.nikolay.stankinschedule.utils.DateUtils.Companion.parseDate
import java.util.*

/**
 * Класс-обертка для доступа к настройкам новостей.
 */
class NewsPreference {

    companion object {

        private const val NEW_PREFERENCE = "news_preference"

        private const val NEWS_DATE = "news_date"

        fun lastNewsUpdate(context: Context, newsSubdivision: Int): Calendar? {
            val date = context.getSharedPreferences(NEW_PREFERENCE, Context.MODE_PRIVATE)
                .getString(NEWS_DATE + "_" + newsSubdivision,null)
            return if (date != null) parseDate(date, supportedDateFormat()) else null
        }

        fun setNewsUpdate(context: Context, newsSubdivision: Int, date: Calendar) {
            context.getSharedPreferences(NEW_PREFERENCE, Context.MODE_PRIVATE)
                .edit()
                .putString(NEWS_DATE + "_" + newsSubdivision, formatDate(date, supportedDateFormat()))
                .apply()
        }

        /**
         * Возвращает поддерживаемый тип формата даты.
         */
        private fun supportedDateFormat() : String {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return DateUtils.FULL_DATE_FORMAT
            }
            return DateUtils.FULL_DATE_FORMAT_API_23
        }
    }
}