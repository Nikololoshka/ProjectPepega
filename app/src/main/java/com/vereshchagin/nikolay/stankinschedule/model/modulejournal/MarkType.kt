package com.vereshchagin.nikolay.stankinschedule.model.modulejournal

import com.google.gson.*
import java.lang.reflect.Type

/**
 * Типы оценок в модульном журнале.
 */
enum class MarkType(val tag: String, val weight: Int) {

    /**
     * Первый модуль.
     */
    FIRST_MODULE("М1", 3),

    /**
     * Второй модуль.
     */
    SECOND_MODULE("М2", 2),

    /**
     * Курсовая работа.
     */
    COURSEWORK("К", 5),

    /**
     * Зачёт.
     */
    CREDIT("З", 5),

    /**
     * Экзамен.
     */
    EXAM("Э", 7);


    /**
     * Сериализатор MarkType.
     */
    class MarkSerializer : JsonSerializer<MarkType> {
        override fun serialize(
            src: MarkType,
            typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement {
            return JsonPrimitive(src.tag)
        }
    }

    /**
     * Десериализатор MarkType.
     */
    class MarkDeserializer : JsonDeserializer<MarkType> {
        @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): MarkType {
            return of(json.asJsonPrimitive.asString)
        }
    }

    companion object {
        /**
         * Возвращает тип оценки из перечисления соответствующего значению ответа от сервера.
         */
        fun of(value: String): MarkType {
            for (type in values()) {
                if (type.tag == value) {
                    return type
                }
            }
            throw IllegalArgumentException("Unknown mark type: $value")
        }
    }
}