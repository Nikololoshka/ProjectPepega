package com.github.nikololoshka.pepegaschedule.settings.subsection;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.github.nikololoshka.pepegaschedule.R;

/**
 * Категориия настроек расписания приложения.
 */
public class SettingsScheduleFragment extends PreferenceFragmentCompat {

    public SettingsScheduleFragment() {
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_schedule, rootKey);
    }
}