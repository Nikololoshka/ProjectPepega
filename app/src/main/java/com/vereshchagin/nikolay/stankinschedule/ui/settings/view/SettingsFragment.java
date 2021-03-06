package com.vereshchagin.nikolay.stankinschedule.ui.settings.view;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.IdRes;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.vereshchagin.nikolay.stankinschedule.R;

/**
 * Фрагмент с настройками приложения.
 */
public class SettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceClickListener {

    private static final String SETTINGS_GENERAL_FRAGMENT = "settings_general_fragment";
    private static final String SETTINGS_SCHEDULE_FRAGMENT = "settings_schedule_fragment";
    private static final String SETTINGS_WIDGET_FRAGMENT = "settings_widget_fragment";
    private static final String SETTINGS_NOTIFICATION_FRAGMENT = "settings_notification_fragment";

    public SettingsFragment() {
        super();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_root, rootKey);

        Preference preferenceGeneral = findPreference(SETTINGS_GENERAL_FRAGMENT);
        if (preferenceGeneral!= null) {
            preferenceGeneral.setOnPreferenceClickListener(this);
        }

        Preference preferenceSchedule = findPreference(SETTINGS_SCHEDULE_FRAGMENT);
        if (preferenceSchedule != null) {
            preferenceSchedule.setOnPreferenceClickListener(this);
        }

        Preference preferenceWidget = findPreference(SETTINGS_WIDGET_FRAGMENT);
        if (preferenceWidget != null) {
            preferenceWidget.setOnPreferenceClickListener(this);
        }

        Preference preferenceNotification = findPreference(SETTINGS_NOTIFICATION_FRAGMENT);
        if (preferenceNotification != null) {
            preferenceNotification.setOnPreferenceClickListener(this);

        }
    }
    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (getActivity() == null) {
            return false;
        }

        // реализация передвижения по настройкам с помощью Navigation graph
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host);

        @IdRes
        int destination;

        switch (preference.getKey()) {
            // к основным настройкам
            case SETTINGS_GENERAL_FRAGMENT:
                destination = R.id.toSettingsGeneralFragment;
                break;
            // к настройкам расписания
            case SETTINGS_SCHEDULE_FRAGMENT:
                destination = R.id.toSettingsScheduleFragment;
                break;
            // к настройкам виджетов
            case SETTINGS_WIDGET_FRAGMENT:
                destination = R.id.toSettingsWidgetFragment;
                break;
            // к настройкам уведомлений
            case SETTINGS_NOTIFICATION_FRAGMENT:
                // android 8.0+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent  intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().getPackageName());
                    startActivity(intent);

                    return true;
                }

                destination = R.id.toSettingsNotificationFragment;
                break;
            default:
                return false;
        }

        // TODO("07/02/21 Ошибка о переходе из "Основных" настроек в настройки "расписания"")
        navController.navigate(destination);
        return true;
    }
}
