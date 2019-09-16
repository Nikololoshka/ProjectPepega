package com.github.nikololoshka.pepegaschedule.schedule.pair;

public enum SubgroupEnum {
    A("A", "(А)"),
    B("B", "(Б)"),
    COMMON("Common", "---");

    private String m_tag;
    private String m_text;

    SubgroupEnum(String tag, String text) {
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
