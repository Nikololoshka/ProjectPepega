package com.github.nikololoshka.pepegaschedule.schedule.model.pair;


public enum TypeEnum {
    LECTURE("Lecture", "Лекция"),
    SEMINAR("Seminar", "Семинар"),
    LABORATORY("Laboratory", "Лабораторная работа");

    private String m_tag;
    private String m_text;

    TypeEnum(String tag, String text) {
        m_tag = tag;
        m_text = text;
    }

    public String tag() {
        return m_tag;
    }

    public String text() {
        return m_text;
    }
}
