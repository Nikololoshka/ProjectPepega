package com.vereshchagin.nikolay.stankinschedule.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vereshchagin.nikolay.stankinschedule.core.domain.settings.ApplicationPreference
import com.vereshchagin.nikolay.stankinschedule.core.domain.settings.DarkMode
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.model.PairColorGroup
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.model.PairColorType
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.repository.SchedulePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val applicationPreference: ApplicationPreference,
    private val schedulePreference: SchedulePreference
) : ViewModel() {

    /**
     * General settings
     */

    private val _nightMode = MutableStateFlow(value = applicationPreference.currentDarkMode())
    val nightMode: StateFlow<DarkMode> = _nightMode.asStateFlow()

    fun setNightMode(mode: DarkMode) {
        applicationPreference.setDarkMode(mode)
        _nightMode.value = mode
    }

    /**
     * Schedule settings
     */

    val isVerticalViewer: Flow<Boolean> = schedulePreference.isVerticalViewer()
    val pairColorGroup: Flow<PairColorGroup> = schedulePreference.scheduleColorGroup()

    fun setVerticalViewer(isVertical: Boolean) {
        viewModelScope.launch { schedulePreference.setVerticalViewer(isVertical) }
    }

    fun setPairColor(hex: String, type: PairColorType) {
        viewModelScope.launch { schedulePreference.setScheduleColor(hex, type) }
    }
}