package com.github.nikololoshka.pepegaschedule.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.github.nikololoshka.pepegaschedule.R;


/**
 * Класс-обертка для доступа к настройкам приложения.
 */
public class ApplicationPreference {

    public static final String LECTURE_COLOR = "schedule_lecture_color";
    public static final String SEMINAR_COLOR = "schedule_seminar_color";
    public static final String LABORATORY_COLOR = "schedule_laboratory_color";
    public static final String SUBGROUP_A_COLOR = "schedule_subgroup_a_color";
    public static final String SUBGROUP_B_COLOR = "schedule_subgroup_b_color";

    public static final String DARK_MODE_SYSTEM_DEFAULT = "pref_system_default";
    public static final String DARK_MODE_BATTERY_SAVER = "pref_battery_saver";
    public static final String DARK_MODE_MANUAL = "pref_manual_mode";

    public static final String SCHEDULE_VIEW_VERTICAL = "pref_vertical";
    public static final String SCHEDULE_VIEW_HORIZONTAL = "pref_horizontal";

    private static final String FIRST_RUN = "first_run";
    private static final String SCHEDULE_VIEW_METHOD = "schedule_view_method";
    private static final String SCHEDULE_LIMIT = "schedule_view_limit";
    private static final String DARK_MODE = "dark_mode";
    private static final String MANUAL_MODE = "manual_mode";


    /**
     * Возвращает текущие созраненое значение режима темной темы.
     * @param context контекст.
     * @return режим темной темы.
     */
    public static String currentDarkMode(@NonNull Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(DARK_MODE, DARK_MODE_SYSTEM_DEFAULT);
    }

    /**
     * Устанавливает значение режима темной темы.
     * @param context контекст.
     * @param darkMode режим темной темы.
     */
    public static void setDarkMode(@NonNull Context context, @NonNull String darkMode) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit()
                .putString(DARK_MODE, darkMode)
                .apply();
    }

    /**
     * Возвращает текущие значение ручного переключателя темной темы.
     * @param context контекст.
     * @return true если темная тема включена, иначе false.
     */
    public static boolean currentManualMode(@NonNull Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(MANUAL_MODE, false);
    }

    /**
     * Устанавливает значение ручного переключателя темной темы.
     * @param context контекст.
     * @param isDarkModeEnabled true если включить темную тему, иначе false.
     */
    public static void setManualMode(@NonNull Context context, boolean isDarkModeEnabled) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit()
                .putBoolean(MANUAL_MODE, isDarkModeEnabled)
                .apply();
    }

    /**
     * Возвращает значение, как должно отображаться расписание.
     * @param context контекст приложения.
     * @return значение отображения.
     */
    public static String scheduleViewMethod(@NonNull Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SCHEDULE_VIEW_METHOD, SCHEDULE_VIEW_VERTICAL);
    }

    /**
     * Возвращает значение, должно ли граничиваться расписание.
     * @param context контекст.
     * @return true - нужно ограничивать, иначе false.
     */
    public static boolean scheduleLimit(@NonNull Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(SCHEDULE_LIMIT, false);
    }

    /**
     * Проверка на первый запуск.
     * @param context контекст приложения.
     * @return первый ли запуск.
     */
    public static boolean firstRun(@NonNull Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(FIRST_RUN, true);
    }

    /**
     * Установить значения для первого запуска. Например, чтобы еще раз запустить приветствие.
     * @param context контекст приложения.
     * @param run значение запуска.
     */
    public static void setFirstRun(@NonNull Context context, boolean run) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit()
                .putBoolean(FIRST_RUN, run)
                .apply();
    }

    /**
     * Вычисляет цвет для пары.
     * @param context контекст.
     * @param preferenceName имя элемента, которому нужен цвет.
     * @return цвет.
     */
    public static int pairColor(@NonNull Context context, String preferenceName) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(preferenceName, defaultColor(context, preferenceName));
    }

    /**
     * Возвращает цвет по умолчанию для пары.
     * @param context контекст.
     * @param preferenceName  имя элемента, которому нужен цвет.
     * @return цвет по умолчанию.
     */
    public static int defaultColor(@NonNull Context context, String preferenceName) {
        int defaultColor = Color.WHITE;
        switch (preferenceName) {
            case LECTURE_COLOR:
                defaultColor = ContextCompat.getColor(context, R.color.colorCardLecture);
                break;
            case SEMINAR_COLOR:
                defaultColor = ContextCompat.getColor(context, R.color.colorCardSeminar);
                break;
            case LABORATORY_COLOR:
                defaultColor = ContextCompat.getColor(context, R.color.colorCardLaboratory);
                break;
            case SUBGROUP_A_COLOR:
                defaultColor = ContextCompat.getColor(context, R.color.colorCardSubgroupA);
                break;
            case SUBGROUP_B_COLOR:
                defaultColor = ContextCompat.getColor(context, R.color.colorCardSubgroupB);
                break;
        }

        return defaultColor;
    }
}
