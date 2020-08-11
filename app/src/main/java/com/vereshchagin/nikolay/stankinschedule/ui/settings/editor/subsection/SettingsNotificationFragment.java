package com.vereshchagin.nikolay.stankinschedule.ui.settings.editor.subsection;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.vereshchagin.nikolay.stankinschedule.R;

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