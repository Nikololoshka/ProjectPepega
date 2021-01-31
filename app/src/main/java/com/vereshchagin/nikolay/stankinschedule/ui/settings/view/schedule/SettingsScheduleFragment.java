package com.vereshchagin.nikolay.stankinschedule.ui.settings.view.schedule;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.vereshchagin.nikolay.stankinschedule.R;

/**
 * Категория настроек расписания приложения.
 */
public class SettingsScheduleFragment extends PreferenceFragmentCompat {

    public SettingsScheduleFragment() {
        super();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_schedule, rootKey);
    }
}