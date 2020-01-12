package com.github.nikololoshka.pepegaschedule.modulejournal.connection;

import androidx.annotation.NonNull;

import com.github.nikololoshka.pepegaschedule.modulejournal.model.StudentData;
import com.github.nikololoshka.pepegaschedule.modulejournal.model.StudentMarks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Парсер ответов от модульного журнала.
 */
public class ModuleJournalJsonParser {

    private static final String SEMESTERS = "semesters";
    private static final String SURNAME = "surname";
    private static final String INITIALS = "initials";
    private static final String GROUP = "stgroup";

    private static final String FACTOR = "factor";
    private static final String DISCIPLINE = "title";
    private static final String TYPE = "num";
    private static final String MARK = "value";

    @NonNull
    public static StudentData parseSemesters(@NonNull String json) throws JSONException {
        JSONObject semestersObject = new JSONObject(json);

        StudentData data = new StudentData();

        JSONArray semestersArray = semestersObject.getJSONArray(SEMESTERS);
        for (int i = 0; i < semestersArray.length(); i++) {
            data.semesters.add(semestersArray.getString(i));
        }

        data.student = semestersObject.getString(SURNAME) + " " + semestersObject.getString(INITIALS);
        data.group = semestersObject.getString(GROUP);

        return data;
    }

    @NonNull
    public static StudentMarks parseMarks(@NonNull String json) throws JSONException {
        JSONArray marksArray = new JSONArray(json);

        StudentMarks marks = new StudentMarks();
        for (int i = 0; i < marksArray.length(); i++) {
            JSONObject markObject = marksArray.getJSONObject(i);

            String discipline = markObject.getString(DISCIPLINE);
            String type = markObject.getString(TYPE);
            double factor = markObject.getDouble(FACTOR);
            int mark = markObject.getInt(MARK);

            marks.addDisciplineMark(discipline, type, mark, factor);
        }

        return marks;
    }
}
