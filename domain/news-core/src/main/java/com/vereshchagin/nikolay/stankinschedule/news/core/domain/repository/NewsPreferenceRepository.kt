package com.vereshchagin.nikolay.stankinschedule.news.core.domain.repository

import org.joda.time.DateTime

interface NewsPreferenceRepository {

    fun updateNewsDateTime(subdivision: Int, time: DateTime = DateTime.now())

    fun currentNewsDateTime(subdivision: Int): DateTime?
}