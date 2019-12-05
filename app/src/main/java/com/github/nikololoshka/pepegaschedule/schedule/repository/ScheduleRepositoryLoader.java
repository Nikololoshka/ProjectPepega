package com.github.nikololoshka.pepegaschedule.schedule.repository;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.io.IOException;
import java.util.TreeMap;

public class ScheduleRepositoryLoader extends AsyncTaskLoader<TreeMap<String, String>> {

    ScheduleRepositoryLoader(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    public TreeMap<String, String> loadInBackground() {
        try {
            final String ROOT = "schedules";
            AssetManager assetManager = getContext().getAssets();
            TreeMap<String, String> loadingSchedules = new TreeMap<>();
            String[] schedules = assetManager.list(ROOT);

            if (schedules != null) {
                for (String schedule : schedules) {
                    loadingSchedules.put(schedule.substring(0, schedule.length() - 5),
                            ROOT + "/" + schedule);
                }
            }

//                String[] grades = assetManager.list(root);
//                for (String grade : Objects.requireNonNull(grades)) {
//                    String[] courses = assetManager.list(root + "/" + grade);
//                    for (String course : Objects.requireNonNull(courses)) {
//                        String[] schedules = assetManager.list(
//                                root + "/" + grade + "/" + course);
//                        for (String schedule : Objects.requireNonNull(schedules)) {
//                            loadingSchedules.put(schedule.substring(0, schedule.length() - 5),
//                                    root + "/" + grade + "/" + course + "/" + schedule);
//                        }
//                    }
//                }

            // Thread.sleep(2000);

            return loadingSchedules;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
