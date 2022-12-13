package com.vereshchagin.nikolay.stankinschedule.core.domain.settings

import org.joda.time.DateTime

interface PreferenceRepository {

    fun getString(key: String): String?

    fun saveString(key: String, value: String)

    fun getDateTime(key: String): DateTime?

    fun saveDateTime(key: String, value: DateTime)
}