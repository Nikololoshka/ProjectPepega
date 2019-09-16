package com.github.nikololoshka.pepegaschedule;

import com.github.nikololoshka.pepegaschedule.schedule.pair.Pair;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;


public class PairUnitTest {

    private final String PATH = "src/test/resources/";

    @Test
    public void loading() {
        try (FileReader reader = new FileReader(PATH + "pair_1.json")){
            Scanner scanner = new Scanner(reader);
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
            }
            JSONObject jsonObject = new JSONObject(builder.toString());

            Pair pair = new Pair();
            pair.load(jsonObject);

            assertTrue("Loading with exception", true);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void saving() {
        try (FileReader reader = new FileReader(PATH + "pair_1.json")){
            Scanner scanner = new Scanner(reader);
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
            }
            JSONObject jsonObject = new JSONObject(builder.toString());

            Pair pair = new Pair();
            pair.load(jsonObject);

            // saving
            JSONObject savedObject = pair.save();

            assertTrue("Saving with exception", true);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadingAndSaving() {
        try (FileReader reader = new FileReader(PATH + "pair_1.json")){
            Scanner scanner = new Scanner(reader);
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
            }
            JSONObject jsonObject = new JSONObject(builder.toString());

            Pair pair;
            for (int i = 0; i < 10; i++) {
                pair = new Pair();
                pair.load(jsonObject);
                jsonObject = pair.save();
            }
            assertTrue("Loading/Saving with exception", true);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}