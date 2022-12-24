package com.vereshchagin.nikolay.stankinschedule.journal.core.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalPreference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val JOURNAL_PREFERENCE = "journal_preference"
private val Context.dataStore by preferencesDataStore(name = JOURNAL_PREFERENCE)

class JournalPreferenceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : JournalPreference {

    override fun isUpdateMarksAllow(): Flow<Boolean> =
        context.dataStore.data.map { preferences -> preferences[UPDATE_MARKS_ALLOW] ?: false }

    override suspend fun setUpdateMarksAllow(allow: Boolean) {
        context.dataStore.edit { preferences -> preferences[UPDATE_MARKS_ALLOW] = allow }
    }

    companion object {
        private val UPDATE_MARKS_ALLOW get() = booleanPreferencesKey("update_marks_allow")
    }
}