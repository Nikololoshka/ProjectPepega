package com.vereshchagin.nikolay.stankinschedule.core.domain.settings

import javax.inject.Inject

class ApplicationPreference @Inject constructor(
    private val manager: PreferenceRepository
) {

    fun currentDarkMode(): DarkMode {
        return DarkMode.from(manager.getString(DARK_MODE)) ?: DarkMode.Default
    }

    fun setDarkMode(mode: DarkMode) {
        manager.saveString(DARK_MODE, mode.tag)
    }

    companion object {
        private const val DARK_MODE = "dark_mode_v2"
    }
}