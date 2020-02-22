package com.vereshchagin.nikolay.stankinschedule.settings.editor.subsection;

import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.vereshchagin.nikolay.stankinschedule.MainActivity;
import com.vereshchagin.nikolay.stankinschedule.MainApplication;
import com.vereshchagin.nikolay.stankinschedule.R;
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference;

/**
 * Категория основные (общих) настроек приложения.
 */
public class SettingsGeneralFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener {

    private static final String DARK_MODE_PREFERENCE = "dark_mode";

    public SettingsGeneralFragment() {
        super();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_general, rootKey);

        ListPreference preferenceDarkMode = findPreference(DARK_MODE_PREFERENCE);
        if (preferenceDarkMode != null) {
            preferenceDarkMode.setOnPreferenceChangeListener(this);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (getActivity() == null) {
            return false;
        }

        if (preference.getKey().equals(DARK_MODE_PREFERENCE)) {
            String darkMode = (String) newValue;

            ApplicationPreference.setDarkMode(getActivity(), darkMode);
            if (darkMode.equals(ApplicationPreference.DARK_MODE_MANUAL)) {
                ApplicationPreference.setManualMode(getActivity(), false);
            }

            MainApplication.getInstance().updateDarkMode();
            if (getActivity() instanceof MainActivity) {
                MainActivity activity = (MainActivity) getActivity();
                activity.updateDarkModeButton();
            }
        }

        return true;
    }
}
