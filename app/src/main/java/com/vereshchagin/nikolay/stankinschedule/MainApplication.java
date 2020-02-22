package com.vereshchagin.nikolay.stankinschedule;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreference;

/**
 * Singleton класс приложения.
 */
public class MainApplication extends Application {

    private static MainApplication singleton = null;

    public static MainApplication getInstance() {
        if(singleton == null) {
            singleton = new MainApplication();
        }
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        updateDarkMode();
        singleton = this;
    }

    /**
     * Устанавливает значение темной темы приложения исходя из настроек.
     */
    public void updateDarkMode() {
        String darkMode = ApplicationPreference.currentDarkMode(this);
        switch (darkMode) {
            case ApplicationPreference.DARK_MODE_SYSTEM_DEFAULT: {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            }
            case ApplicationPreference.DARK_MODE_BATTERY_SAVER: {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                break;
            }
            case ApplicationPreference.DARK_MODE_MANUAL: {
                boolean isDark = ApplicationPreference.currentManualMode(this);
                AppCompatDelegate.setDefaultNightMode(isDark ?
                        AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                break;
            }
        }
    }
}
