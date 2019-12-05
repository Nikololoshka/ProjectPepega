package com.github.nikololoshka.pepegaschedule.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.github.nikololoshka.pepegaschedule.schedule.pair.SubgroupEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class SchedulePreference {
    public static final String ROOT_PATH = "schedules";

    private static final String SCHEDULE_PREFERENCE = "schedule_preference";
    private static final String FAVORITE_SCHEDULE = "favorite_schedule";
    private static final String SCHEDULES = "schedules";
    private static final String SUBGROUP = "subgroup";

    private static ArrayList<String> mSchedulesList = null;
    private static String mFavoriteSchedule = null;
    private static SubgroupEnum mSubgroup = null;

    private static long mChangeCount = 0;

    public static void add(@NonNull Context context, String scheduleName) {
        if (mSchedulesList == null) {
            load(context);
        }

        mSchedulesList.add(scheduleName);
        save(context);
    }

    public static void move(@NonNull Context context, int fromPosition, int toPosition) {
        if (mSchedulesList == null) {
            load(context);
        }

        Collections.swap(mSchedulesList, fromPosition, toPosition);
        save(context);
    }

    public static void remove(@NonNull Context context, String scheduleName) {
        if (mSchedulesList == null) {
            load(context);
        }

        if (scheduleName.equals(mFavoriteSchedule)) {
            setFavorite(context, "");
        }

        mSchedulesList.remove(scheduleName);
        save(context);
    }

    public static boolean contains(@NonNull Context context, String scheduleName) {
        if (mSchedulesList == null) {
            load(context);
        }

        return mSchedulesList.contains(scheduleName);
    }

    public static List<String> schedules(@NonNull Context context) {
        if (mSchedulesList == null) {
            load(context);
        }
        return mSchedulesList;
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

    public static void setSubgroup(@NonNull Context context, SubgroupEnum subgroup) {
        if (mSubgroup == null) {
            load(context);
        }

        if (mSubgroup == subgroup) {
            return;
        }

        mSubgroup = subgroup;
        save(context);
    }

    public static SubgroupEnum subgroup(@NonNull Context context) {
        if (mSubgroup == null) {
            load(context);
        }

        return mSubgroup;
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

        mSubgroup = SubgroupEnum.COMMON;
        String tag = PREFERENCES.getString(SUBGROUP, SubgroupEnum.COMMON.tag());
        for (SubgroupEnum value : SubgroupEnum.values()) {
            if (value.tag().equals(tag)) {
                mSubgroup = value;
                break;
            }
        }
    }

    private static void save(@NonNull Context context) {
        SharedPreferences PREFERENCES =
                context.getSharedPreferences(SCHEDULE_PREFERENCE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = PREFERENCES.edit();
        editor.putString(SCHEDULES, TextUtils.join(";", mSchedulesList));
        editor.putString(FAVORITE_SCHEDULE, mFavoriteSchedule);
        editor.putString(SUBGROUP, mSubgroup.tag());
        editor.apply();

        ++mChangeCount;
    }

    public static List<String> banCharacters() {
        return Arrays.asList(";", "/");
    }

    public static long changeCount() {
        return mChangeCount;
    }

    public static void addChange() {
        ++mChangeCount;
    }

    @NonNull
    public static String createPath(@NonNull Context context, String scheduleName) {
        return new File(scheduleDir(context), scheduleName + fileExtension()).getAbsolutePath();
    }

    public static File scheduleDir(@NonNull Context context) {
        return context.getExternalFilesDir(ROOT_PATH);
    }

    public static String fileExtension() {
        return ".json";
    }
}

