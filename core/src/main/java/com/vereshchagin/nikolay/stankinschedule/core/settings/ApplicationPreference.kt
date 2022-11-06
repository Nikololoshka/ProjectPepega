package com.vereshchagin.nikolay.stankinschedule.core.settings

import com.vereshchagin.nikolay.stankinschedule.core.ui.PreferenceManager
import javax.inject.Inject

class ApplicationPreference @Inject constructor(
    private val manager: PreferenceManager
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