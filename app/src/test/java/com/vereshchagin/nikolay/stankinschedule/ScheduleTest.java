package com.vereshchagin.nikolay.stankinschedule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.vereshchagin.nikolay.stankinschedule.schedule.model.Schedule;
import com.vereshchagin.nikolay.stankinschedule.schedule.model.pair.Pair;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertTrue;

/**
 * Тесты связанные с расписанием.
 */
public class ScheduleTest {

    private static final String PATH = "src/test/resources/";

    @Test
    public void loading() {
        Schedule schedule = new Schedule();
        schedule.addPair(loadPair("pair_1.json"));
        assertTrue(true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void impossiblePairs() {
        Schedule schedule = new Schedule();
        schedule.addPair(loadPair("pair_1.json"));
        schedule.addPair(loadPair("pair_2.json"));
    }

    @Test
    public void possiblePairs() {
        Schedule schedule = new Schedule();
        schedule.addPair(loadPair("pair_2.json"));
        schedule.addPair(loadPair("pair_3.json"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void impossibleIntersect() {
        Schedule schedule = new Schedule();
        schedule.addPair(loadPair("pair_4.json"));
        schedule.addPair(loadPair("pair_5.json"));
        schedule.addPair(loadPair("pair_6.json"));
    }

    /**
     * Загружает пару из файла.
     * @param filename имя файла.
     * @return пара.
     */
    @Nullable
    private Pair loadPair(@NonNull String filename) {
        try {
            File file = org.apache.commons.io.FileUtils.getFile(PATH, filename);
            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

            return new GsonBuilder()
                    .registerTypeAdapter(Pair.class, new Pair.PairDeserialize())
                    .create()
                    .fromJson(json, Pair.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
