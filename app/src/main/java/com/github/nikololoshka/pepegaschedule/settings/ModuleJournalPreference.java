package com.github.nikololoshka.pepegaschedule.settings;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

/**
 * Класс-обертка для доступа к настройкам модульного журнала.
 */
public class ModuleJournalPreference {

    private static final String MODULE_JOURNAL_PREFERENCE = "module_journal_preference";

    private static final String SIGN_IN = "sign_in";

    public static void setSignIn(@NonNull Context context, boolean sign) {
        SharedPreferences preferences =
                context.getSharedPreferences(MODULE_JOURNAL_PREFERENCE, Context.MODE_PRIVATE);

        preferences.edit()
                .putBoolean(SIGN_IN, sign)
                .apply();
    }

    public static boolean signIn(@NonNull Context context) {
        return context.getSharedPreferences(MODULE_JOURNAL_PREFERENCE, Context.MODE_PRIVATE)
                .getBoolean(SIGN_IN, false);
    }


}
