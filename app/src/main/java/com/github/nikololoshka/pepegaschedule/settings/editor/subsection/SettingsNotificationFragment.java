package com.github.nikololoshka.pepegaschedule.settings.editor.subsection;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.github.nikololoshka.pepegaschedule.R;

/**
 * Категория настроек уведомлений приложения.
 */
public class SettingsNotificationFragment extends PreferenceFragmentCompat {

    public SettingsNotificationFragment() {
        super();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_notification, rootKey);
    }
}