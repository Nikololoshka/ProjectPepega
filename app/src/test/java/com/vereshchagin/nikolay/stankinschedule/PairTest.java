package com.vereshchagin.nikolay.stankinschedule;

import com.google.gson.GsonBuilder;
import com.vereshchagin.nikolay.stankinschedule.schedule.model.pair.Pair;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertTrue;

/**
 * Тесты связанные с парой.
 */
public class PairTest {

    private static final String PATH = "src/test/resources/";

    /**
     * Проверка на правильность загрузки/сохранения пары.
     */
    @Test
    public void loadingAndSaving() {
        try {
            for (int i = 1; i <= 6; i++) {
                File file = FileUtils.getFile(PATH, String.format("pair_%d.json", i));
                String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

                Pair pair = new GsonBuilder()
                        .registerTypeAdapter(Pair.class, new Pair.PairDeserialize())
                        .create()
                        .fromJson(json, Pair.class);

                new GsonBuilder()
                        .registerTypeAdapter(Pair.class, new Pair.PairSerialize())
                        .setPrettyPrinting()
                        .create()
                        .toJson(pair);
            }

            assertTrue("Successfully!", true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}