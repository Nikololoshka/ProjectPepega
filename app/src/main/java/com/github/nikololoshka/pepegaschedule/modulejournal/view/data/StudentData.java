package com.github.nikololoshka.pepegaschedule.modulejournal.view.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.nikololoshka.pepegaschedule.modulejournal.network.SemestersResponse;
import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Информация о студенте.
 */
public class StudentData {

    private static final String STUDENT_FOLDER = "student_data";

    public static void saveCacheData(@NonNull SemestersResponse response, @Nullable File cacheDirectory) {
        if (cacheDirectory == null) {
            return;
        }

        File cacheFile = FileUtils.getFile(cacheDirectory,STUDENT_FOLDER, "student.json");
        String json = new Gson().toJson(response);

        try {
            FileUtils.writeStringToFile(cacheFile, json, StandardCharsets.UTF_8, false);
        } catch (IOException ignored) {

        }
    }

    @Nullable
    public static SemestersResponse loadCacheData(@Nullable File cacheDirectory) {
        if (cacheDirectory == null) {
            return null;
        }

        File cacheFile = FileUtils.getFile(cacheDirectory, STUDENT_FOLDER, "student.json");
        if (!cacheFile.exists()) {
            return null;
        }

        try {
            String json = FileUtils.readFileToString(cacheFile, StandardCharsets.UTF_8);
            return new Gson().fromJson(json, SemestersResponse.class);
        } catch (IOException ignored) {

        }

        return null;
    }
}
