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

    public static final String SCHEDULE_VIEW_VERTICAL = "pref_vertical";
    public static final String SCHEDULE_VIEW_HORIZONTAL = "pref_horizontal";

    private static final String FIRST_RUN = "first_run";
    private static final String SCHEDULE_VIEW_METHOD = "schedule_view_method";
    private static final String SCHEDULE_WIDGETS = "schedule_app_widgets";

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

//    /**
//     * Сохраняет ID виджета с расписанием в список.
//     * @param context контекст.
//     * @param scheduleID ID виджета.
//     */
//    public static void addScheduleWidget(@NonNull Context context, int scheduleID) {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//
//        Set<String> widgets = preferences.getStringSet(SCHEDULE_WIDGETS, new HashSet<String>());
//        widgets.add(String.valueOf(scheduleID));
//
//        preferences.edit()
//                .putStringSet(SCHEDULE_WIDGETS, widgets)
//                .apply();
//    }
//
//    /**
//     * Удаляет ID виджета с расписанием из списка.
//     * @param context контекст.
//     * @param scheduleID ID виджета.
//     */
//    public static void removeScheduleWidget(@NonNull Context context, int scheduleID) {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//
//        Set<String> widgets = preferences.getStringSet(SCHEDULE_WIDGETS, new HashSet<String>());
//        widgets.remove(String.valueOf(scheduleID));
//
//        preferences.edit()
//                .putStringSet(SCHEDULE_WIDGETS, widgets)
//                .apply();
//    }
//
//    /**
//     * Возвращает список ID виджетов с расписанями.
//     * @param context контекст.
//     * @return список ID.
//     */
//    public static ArrayList<Integer> scheduleWidgetIDs(@NonNull Context context) {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//
//        Set<String> widgets = preferences.getStringSet(SCHEDULE_WIDGETS, new HashSet<String>());
//
//        ArrayList<Integer> ids = new ArrayList<>();
//        for (String id : widgets) {
//            ids.add(Integer.valueOf(id));
//        }
//
//        return ids;
//    }
//
//    /**
//     * Очищает весь список ID виджетов.
//     * @param context контекст.
//     */
//    public static void clearScheduleWidgetIDs(@NonNull Context context) {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        preferences.edit()
//                .remove(SCHEDULE_WIDGETS)
//                .apply();
//    }
}
