package com.vereshchagin.nikolay.stankinschedule.core.domain.settings

import org.joda.time.DateTime

interface PreferenceRepository {

    fun getBoolean(key: String, default: Boolean): Boolean

    fun saveBoolean(key: String, value: Boolean)

    fun getString(key: String): String?

    fun saveString(key: String, value: String)

    fun getDateTime(key: String): DateTime?

    fun saveDateTime(key: String, value: DateTime)
}