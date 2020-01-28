package com.github.nikololoshka.pepegaschedule.schedule.repository;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Загрузчик расписаний для репозитория.
 */
public class ScheduleRepositoryLoader extends AsyncTaskLoader<List<RepositoryItem>> {

    ScheduleRepositoryLoader(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    public List<RepositoryItem> loadInBackground() {

        try {
            final String ROOT = "schedules";

            // локальный репозиторий
            AssetManager assetManager = getContext().getAssets();
            ArrayList<RepositoryItem> repositoryItems= new ArrayList<>();
            String[] schedules = assetManager.list(ROOT);

            if (schedules != null) {
                for (String schedule : schedules) {
                    RepositoryItem item = new RepositoryItem();
                    item.setName(schedule.substring(0, schedule.length() - 5));
                    item.setPath(ROOT + "/" + schedule);
                    repositoryItems.add(item);
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

            return repositoryItems;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
