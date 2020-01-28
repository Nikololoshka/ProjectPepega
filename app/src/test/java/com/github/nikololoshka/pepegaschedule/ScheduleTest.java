package com.github.nikololoshka.pepegaschedule;

import com.github.nikololoshka.pepegaschedule.schedule.model.Schedule;
import com.github.nikololoshka.pepegaschedule.schedule.model.pair.Pair;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;


public class ScheduleTest {
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

    public Pair loadPair(String filename) {
        final String PATH = "src/test/resources/";

        try (FileReader reader = new FileReader(PATH + filename)){
            Scanner scanner = new Scanner(reader);
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
            }

            JSONObject jsonObject = new JSONObject(builder.toString());
            Pair pair = new Pair();
            pair.load(jsonObject);

            return pair;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
