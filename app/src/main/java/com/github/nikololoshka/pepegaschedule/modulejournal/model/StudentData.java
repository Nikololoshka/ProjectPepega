package com.github.nikololoshka.pepegaschedule.modulejournal.model;

import androidx.annotation.NonNull;

import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * Информация о студенте.
 */
public class StudentData {

    public ArrayList<String> semesters;
    public String student;
    public String group;

    public StudentData() {
        semesters = new ArrayList<>();
        student = "";
        group = "";
    }

    @NonNull
    @Override
    public String toString() {
        return MessageFormat.format("{0} {1}. {2}", student, group, semesters);
    }
}
