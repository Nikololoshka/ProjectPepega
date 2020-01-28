package com.github.nikololoshka.pepegaschedule.schedule.model.pair;

public enum FrequencyEnum {
    ONCE("once", "", 1),
    EVERY("every", "к.н.", 7),
    THROUGHOUT("throughout", "ч.н.",14);

    private String m_tag;
    private String m_text;
    private int m_period;


    FrequencyEnum(String tag, String text, int period) {
        m_tag = tag;
        m_text = text;
        m_period = period;
    }

    public String tag() {
        return m_tag;
    }

    public String text() {
        return m_text;
    }

    public int period() {
        return m_period;
    }
}