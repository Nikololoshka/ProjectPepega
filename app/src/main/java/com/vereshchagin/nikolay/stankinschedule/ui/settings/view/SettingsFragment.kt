package com.vereshchagin.nikolay.stankinschedule.ui.settings.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.IdRes
import androidx.navigation.Navigation
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.vereshchagin.nikolay.stankinschedule.R

/**
 * Фрагмент с настройками приложения.
 */
class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_root, rootKey)

        findPreference<Preference>(SETTINGS_GENERAL_FRAGMENT)?.onPreferenceClickListener = this
        findPreference<Preference>(SETTINGS_SCHEDULE_FRAGMENT)?.onPreferenceClickListener = this
        findPreference<Preference>(SETTINGS_WIDGET_FRAGMENT)?.onPreferenceClickListener = this
        findPreference<Preference>(SETTINGS_NOTIFICATION_FRAGMENT)?.onPreferenceClickListener = this

    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        // реализация передвижения по настройкам с помощью Navigation graph
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host)

        @IdRes
        val destination: Int = when (preference.key) {
            SETTINGS_GENERAL_FRAGMENT -> R.id.toSettingsGeneralFragment
            SETTINGS_SCHEDULE_FRAGMENT -> R.id.toSettingsScheduleFragment
            SETTINGS_WIDGET_FRAGMENT -> R.id.toSettingsWidgetFragment
            SETTINGS_NOTIFICATION_FRAGMENT -> {
                // android 8.0+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                    startActivity(intent)
                    return true
                }
                R.id.toSettingsNotificationFragment
            }
            else -> return false
        }

        // TODO("07/02/21 Ошибка о переходе из "Основных" настроек в настройки "расписания"")
        navController.navigate(destination)
        return true
    }

    companion object {
        private const val SETTINGS_GENERAL_FRAGMENT = "settings_general_fragment"
        private const val SETTINGS_SCHEDULE_FRAGMENT = "settings_schedule_fragment"
        private const val SETTINGS_WIDGET_FRAGMENT = "settings_widget_fragment"
        private const val SETTINGS_NOTIFICATION_FRAGMENT = "settings_notification_fragment"
    }
}