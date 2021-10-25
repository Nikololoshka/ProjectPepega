package com.vereshchagin.nikolay.stankinschedule.ui.settings.view.notification

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.vereshchagin.nikolay.stankinschedule.R

/**
 * Категория настроек уведомлений приложения.
 */
class SettingsNotificationFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
        setPreferencesFromResource(R.xml.preferences_notification, rootKey)
    }
}