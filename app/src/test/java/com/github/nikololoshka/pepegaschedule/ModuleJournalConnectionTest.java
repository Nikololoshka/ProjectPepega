package com.github.nikololoshka.pepegaschedule;

import com.github.nikololoshka.pepegaschedule.modulejournal.connection.ModuleJournalConnection;
import com.github.nikololoshka.pepegaschedule.modulejournal.connection.ModuleJournalConnectionException;
import com.github.nikololoshka.pepegaschedule.modulejournal.connection.ModuleJournalJsonParser;
import com.github.nikololoshka.pepegaschedule.modulejournal.model.StudentData;
import com.github.nikololoshka.pepegaschedule.modulejournal.model.StudentMarks;

import org.json.JSONException;
import org.junit.Test;

import java.io.IOException;

/**
 * Тестирование соединения с мдульным журналом.
 */
public class ModuleJournalConnectionTest {
    
    private static final String LOGIN = "117022";
    private static final String PASSWORD = "stankin117022";
    
    @Test
    public void successfulReceiptSemesters() throws IOException, ModuleJournalConnectionException, JSONException {
        ModuleJournalConnection mj = new ModuleJournalConnection();
        String response = mj.requestSemesters(LOGIN, PASSWORD);
        StudentData data = ModuleJournalJsonParser.parseSemesters(response);

        System.out.println(data);
    }

    @Test
    public void successfulReceiptMarks() throws IOException, ModuleJournalConnectionException, JSONException {
        ModuleJournalConnection mj = new ModuleJournalConnection();
        String response = mj.requestMarks(LOGIN, PASSWORD, "2019-осень");
        StudentMarks marks = ModuleJournalJsonParser.parseMarks(response);
        System.out.println(marks.toString());
    }

    @Test(expected = ModuleJournalConnectionException.class)
    public void unsuccessfulLogging() throws IOException, ModuleJournalConnectionException {
        ModuleJournalConnection mj = new ModuleJournalConnection();
        mj.requestSemesters("117100", PASSWORD);
    }
}