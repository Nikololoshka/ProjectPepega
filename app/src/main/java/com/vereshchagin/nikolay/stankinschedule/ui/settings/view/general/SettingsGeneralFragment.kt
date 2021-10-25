package com.vereshchagin.nikolay.stankinschedule.ui.settings.view.general

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.vereshchagin.nikolay.stankinschedule.MainActivity
import com.vereshchagin.nikolay.stankinschedule.MainApplication
import com.vereshchagin.nikolay.stankinschedule.R
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreferenceKt
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Категория основные (общих) настроек приложения.
 */
@AndroidEntryPoint
class SettingsGeneralFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

    @Inject
    lateinit var applicationPreference: ApplicationPreferenceKt

    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
        setPreferencesFromResource(R.xml.preferences_general, rootKey)

        findPreference<ListPreference>(DARK_MODE_PREFERENCE)?.onPreferenceChangeListener = this
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        if (preference.key == DARK_MODE_PREFERENCE) {
            val darkMode = newValue as String

            applicationPreference.darkMode = darkMode
            if (darkMode == ApplicationPreferenceKt.DARK_MODE_MANUAL) {
                applicationPreference.isManualDarkModeEnabled = false
            }

            MainApplication.instance.updateDarkMode()

            val currentActivity = requireActivity()
            if (currentActivity is MainActivity) {
                currentActivity.updateDarkModeButton()
            }
        }
        return true
    }

    companion object {
        private const val DARK_MODE_PREFERENCE = "dark_mode"
    }
}