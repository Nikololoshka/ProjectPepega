package com.vereshchagin.nikolay.stankinschedule.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SchedulePreference {

    public static final String ROOT_PATH = "schedules";

    private static final String MIGRATE_SCHEDULE = "migrate_schedule";

    private static final String SCHEDULE_PREFERENCE = "schedule_preference";
    private static final String FAVORITE_SCHEDULE = "favorite_schedule";
    private static final String SCHEDULES = "schedules";
    private static final String SUBGROUP = "subgroup";

    private static ArrayList<String> mSchedulesList = null;
    private static String mFavoriteSchedule = null;

    private static long mChangeCount = 0;

    public static boolean migrateSchedule(@NonNull Context context) {
        SharedPreferences PREFERENCES =
                context.getSharedPreferences(SCHEDULE_PREFERENCE, Context.MODE_PRIVATE);
        return PREFERENCES.getBoolean(MIGRATE_SCHEDULE, false);
    }

    public static void setMigrateSchedule(@NonNull Context context, boolean migrate) {
        SharedPreferences PREFERENCES =
                context.getSharedPreferences(SCHEDULE_PREFERENCE, Context.MODE_PRIVATE);
        PREFERENCES.edit()
                .putBoolean(MIGRATE_SCHEDULE, migrate)
                .apply();
    }


    @Deprecated
    public static List<String> schedules(@NonNull Context context) {
        if (mSchedulesList == null) {
            load(context);
        }
        return new ArrayList<>(mSchedulesList);
    }

    public static void setFavorite(@NonNull Context context, String favoriteSchedule) {
        if (mFavoriteSchedule == null) {
            load(context);
        }

        mFavoriteSchedule = favoriteSchedule;
        save(context);
    }

    public static String favorite(@NonNull Context context) {
        if (mFavoriteSchedule == null) {
            load(context);
        }

        return mFavoriteSchedule;
    }

    private static void load(@NonNull Context context) {
        SharedPreferences PREFERENCES =
                context.getSharedPreferences(SCHEDULE_PREFERENCE, Context.MODE_PRIVATE);

        String schedulesString = PREFERENCES.getString(SCHEDULES, "");
        ArrayList<String> schedules = new ArrayList<>();

        if (!schedulesString.isEmpty()) {
            schedules = new ArrayList<>(Arrays.asList(schedulesString.split(";")));
        }

        mSchedulesList = schedules;
        mFavoriteSchedule = PREFERENCES.getString(FAVORITE_SCHEDULE, "");
    }

    private static void save(@NonNull Context context) {
        SharedPreferences preferences =
                context.getSharedPreferences(SCHEDULE_PREFERENCE, Context.MODE_PRIVATE);

        preferences.edit()
                .putString(SCHEDULES, TextUtils.join(";", mSchedulesList))
                .putString(FAVORITE_SCHEDULE, mFavoriteSchedule)
                .apply();

        ++mChangeCount;
    }

    public static List<String> banCharacters() {
        return Arrays.asList(";", "/");
    }

    @Deprecated
    @NonNull
    public static String createPath(@NonNull Context context, String scheduleName) {
        return new File(scheduleDir(context), scheduleName + fileExtension()).getAbsolutePath();
    }

    @Deprecated
    public static File scheduleDir(@NonNull Context context) {
        return context.getExternalFilesDir(ROOT_PATH);
    }

    @Deprecated
    public static String fileExtension() {
        return ".json";
    }
}

