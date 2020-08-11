package com.vereshchagin.nikolay.stankinschedule.ui.modulejournal.view.model;

import androidx.annotation.NonNull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;

import java.lang.reflect.Type;

/**
 * Типы оценок в модульном журнале.
 */
public enum MarkType {

    /**
     * Первый модуль.
     */
    @Expose
    FIRST_MODULE("М1"),

    /**
     * Второй модуль.
     */
    @Expose
    SECOND_MODULE("М2"),

    /**
     * Курсовая работа.
     */
    @Expose
    COURSEWORK("К"),

    /**
     * Зачёт.
     */
    @Expose
    CREDIT("З"),

    /**
     * Экзамен.
     */
    @Expose
    EXAM("Э");


    @NonNull
    private String mTag;

    MarkType(@NonNull String tag) {
        mTag = tag;
    }

    /**
     * Возвращает тип оценки из перечисления соответстующего значению ответа от сервера.
     * @param value ответ от сервера.
     * @return тип оценки.
     */
    @NonNull
    public static MarkType of(@NonNull String value) {
        for (MarkType type : MarkType.values()) {
            if (type.mTag.equals(value)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unknown mark type: " + value);
    }

    @NonNull
    @Override
    public String toString() {
        return mTag;
    }

    /**
     * Правило сериализации MarkType.
     */
    public static class MarkSerialize implements JsonSerializer<MarkType> {

        @Override
        public JsonElement serialize(MarkType src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }

    /**
     * Правило десериализации MarkType.
     */
    public static class MarkDeserialize implements JsonDeserializer<MarkType> {

        @Override
        public MarkType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return MarkType.of(json.getAsJsonPrimitive().getAsString());
        }
    }
}
