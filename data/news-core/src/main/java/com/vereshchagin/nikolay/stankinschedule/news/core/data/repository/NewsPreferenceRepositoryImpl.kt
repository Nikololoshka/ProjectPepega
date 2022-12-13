package com.vereshchagin.nikolay.stankinschedule.news.core.data.repository

import com.vereshchagin.nikolay.stankinschedule.core.data.preference.PreferenceManager
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.repository.NewsPreferenceRepository
import org.joda.time.DateTime
import javax.inject.Inject

class NewsPreferenceRepositoryImpl @Inject constructor(
    private val preference: PreferenceManager
) : NewsPreferenceRepository {

    override fun updateNewsDateTime(subdivision: Int, time: DateTime) {
        preference.saveDateTime("news_$subdivision", time)
    }

    override fun currentNewsDateTime(subdivision: Int): DateTime? {
        return preference.getDateTime("news_$subdivision")
    }
}