package com.vereshchagin.nikolay.stankinschedule.news.viewer.repository

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

/**
 * Генератор HTML разметки новости из ответа от сервера.
 */
class HtmlGenerator {

    /**
     * Базовый класс тэга в HTML
     * @param tag тэг.
     * @param content контент внутри тэга.
     * @param attributes атрибуты тэга (стили и т.п.).
     */
    private open class BaseTag(val tag: String, val content: String, var attributes: String)

    /**
     * Общий тэг разметки.
     */
    private class CommonTag(
        tag: String, content: String, attributes: String = ""
    ) : BaseTag(tag, content, attributes) {
        override fun toString(): String {
            return "<$tag $attributes>$content</$tag>"
        }
    }

    /**
     * Краткий тэг разметки для медиа ресурсов.
     */
    private class MediaTag(
        tag: String, content: String, attributes: String = ""
    ) : BaseTag(tag, content, attributes) {
        override fun toString(): String {
            return "<$tag $attributes src=\"$content\" />"
        }
    }

    companion object {

        private const val INSERT = "insert"
        private const val ATTRIBUTES = "attributes"

        /**
         * Атрибуты, уоторые просто добавляютя в тэг.
         */
        private val ATTRIBUTES_TAG_SET = setOf(
            "height",
            "direction",
            "alt",
            "width",
            "align"
        )

        /**
         * Атрибуты CSS, которые необходимо сгенерировать.
         */
        private val ATTRIBUTES_CSS_SET = setOf(
            "color",
            "background"
        )

        /**
         * Атрибуты CSS, которые заменяются на соответвующие значение.
         */
        private val ATTRIBUTES_CSS_MAP = mapOf(
            "bold" to "font-weight: bold",
            "italic" to "font-style: italic",
            "underline" to "text-decoration: underline",
            "strike" to "text-decoration: line-through"
        )

        /**
         * Генерирует HTML из JSON объекта.
         * @param ops JSON массив с элементами новости.
         * @return HTML разметка в виде строки.
         */
        fun generate(ops: JsonArray): String {
            var html = ""

            for (element in ops) {
                val content = element.asJsonObject
                val attributes = content.get(ATTRIBUTES)?.asJsonObject
                val insert = content.get(INSERT)

                val tag = tagGenerator(attributes, insert)
                html += tag.toString() + "\n"
            }

            return html
        }

        /**
         * Создает тэг соответсвующему контенту.
         * @param attributes JSON объект с атрибутами тэга.
         * @param insert JSON объект с контентом тэга.
         * @return объект тэга элемента.
         */
        private fun tagGenerator(attributes: JsonObject?, insert: JsonElement): BaseTag {
            var tag: BaseTag? = null

            // есть медиа
            if (insert.isJsonObject) {
                val media = insert.asJsonObject
                if (media != null) {
                    when {
                        // видео
                        media.has("video") -> {
                            tag = MediaTag("video", media["video"].asString)
                        }
                        // картинка
                        media.has("image") -> {
                            tag = MediaTag("img", media["image"].asString)
                        }
                    }
                }
            } else {
                // ссылка
                if (attributes != null && attributes.has("link")) {
                    tag = CommonTag("a", insert.asString)
                }
            }

            // просто параграф с текстом
            if (tag == null) {
                tag = CommonTag("p", if (insert.isJsonPrimitive) insert.asString else "")
            }

            tag.attributes = attributesGenerator(attributes)

            return tag
        }

        /**
         * Создает атрибуты для тэга.
         * @param attributes JSON объект с аттрибутами.
         * @return строка с атрибутами для тэга.
         */
        private fun attributesGenerator(attributes: JsonObject?): String {
            val attrs = HashMap<String, String>()
            var style = ""

            if (attributes != null) {
                // атрибут ссылка
                if (attributes.has("link")) {
                    attrs["href"] = attributes["link"].asString
                }

                // атрибуты "как есть"
                for (key in attributes.keySet().intersect(ATTRIBUTES_TAG_SET)) {
                    attrs[key] = attributes[key].asString
                }

                // получение готового CSS
                if (attributes.has("style")) {
                    style = attributes["style"].asString
                }

                // дополнение "готовых" CSS атрибутов
                for (key in attributes.keySet().intersect(ATTRIBUTES_CSS_SET)) {
                    style += " " + key  + ": " + attributes[key].asString + ";"
                }

                // дополнение "сырых" CSS атрибутов
                for (key in attributes.keySet().intersect(ATTRIBUTES_CSS_MAP.keys)) {
                    style += " " + ATTRIBUTES_CSS_MAP[key] + ";"
                }
            }

            var result = "style=\"$style\" "
            for ((key, value) in attrs) {
                result += " $key=\"$value\""
            }

            return result
        }
    }
}