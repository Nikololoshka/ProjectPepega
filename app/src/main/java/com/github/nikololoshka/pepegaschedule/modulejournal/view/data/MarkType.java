package com.github.nikololoshka.pepegaschedule.modulejournal.view.data;

import androidx.annotation.NonNull;

/**
 * Типы оценок в модульном журнале.
 */
public enum MarkType {

    /**
     * Первый модуль.
     */
    FIRST_MODULE,

    /**
     * Второй модуль.
     */
    SECOND_MODULE,

    /**
     * Курсовая работа.
     */
    COURSEWORK,

    /**
     * Зачёт.
     */
    CREDIT,

    /**
     * Экзамен.
     */
    EXAM;

    /**
     * Возвращает тип оценки из перечисления соответстующего значению ответа от сервера.
     * @param value ответ от сервера.
     * @return тип оценки.
     */
    public static MarkType of(@NonNull String value) {
        switch (value) {
            case "М1":
                return FIRST_MODULE;
            case "М2":
                return SECOND_MODULE;
            case "К":
                return COURSEWORK;
            case "З":
                return CREDIT;
            case "Э":
                return EXAM;
        }
        throw new IllegalArgumentException("Unknown mark type: " + value);
    }
}
