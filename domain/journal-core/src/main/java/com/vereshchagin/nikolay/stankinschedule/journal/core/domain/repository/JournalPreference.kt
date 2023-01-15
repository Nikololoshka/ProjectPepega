package com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface JournalPreference {

    fun isUpdateMarksAllow(): Flow<Boolean>

    suspend fun setUpdateMarksAllow(allow: Boolean)
}