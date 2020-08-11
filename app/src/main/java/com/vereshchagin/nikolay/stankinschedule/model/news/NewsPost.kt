package com.vereshchagin.nikolay.stankinschedule.model.news

import com.google.gson.*
import com.vereshchagin.nikolay.stankinschedule.repository.NewsPostRepository
import java.lang.reflect.Type


/**
 * Новостной пост.
 * @param id номер поста.
 * @param date дата поста.
 * @param title заголовок новости.
 * @param logo относительный путь к картинке новости.
 * @param text текст новости.
 * @param delta разметка новости.
 */
class NewsPost(
    val id: Int,
    val date: String,
    val title: String,
    val logo: String,
    val text: String,
    val delta: String
) {

    /**
     * Возвращает HTML страницу для Quill редактора.
     */
    fun quillPage(): String {
        return "<!DOCTYPE html>\n" +
            "<html lang=\"ru\">\n" +
            "    <head>\n" +
            "        <meta charset='UTF-8'>\n" +
            "        <meta name='viewport' content='width=device-width, initial-scale=1'>\n" +
            "        <link rel=\"stylesheet\" href=\"file:///android_asset/news/quill.css\"> " +
            "        <script src=\"file:///android_asset/news/quill.min.js\" type=\"text/javascript\"></script>\n" +
            "    </head>\n" +
            "    <body>\n" +
            "        <div id=\"raw-text\">$text</div>\n" +
            "        <div id=\"editor\"></div>\n" +
            "        <script>\n" +
            "            var delta = $delta\n" +
            "            var quill = new Quill('#editor', { readOnly: true });\n" +
            "            quill.setContents(delta);\n" +
            "            Android.onNewsLoaded();  " +
            "        </script>\n" +
            "    </body>\n" +
            "</html>"
    }

    /**
     * Возвращает url к картинке новости.
     */
    fun logoUrl() = NewsPostRepository.BASE_URL + logo

    /**
     * Возвращает только дату из публикации.
     */
    fun onlyDate() = date.split(" ").first()

    /**
     * Десериализатор объекта NewsPost из JSON.
     */
    class NewsPostDeserializer: JsonDeserializer<NewsPost> {
        override fun deserialize(
            json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?
        ): NewsPost {
            val rootObject = json?.asJsonObject ?:
                throw JsonParseException("Response is not JSON object")

            val id = rootObject.get("id")?.asInt
            val date = rootObject.get("date")?.asString
            val title = rootObject.get("title")?.asString
            val logo = rootObject.get("logo")?.asString

            var text = ""
            try {
                val textObject = rootObject.get("text")
                if (textObject != null && !textObject.isJsonNull) {
                    text = rootObject.get("text").asString
                }
            } catch (ignored: Exception) {

            }

            if (id == null || date == null || title == null || logo == null) {
                throw JsonParseException("JSON object has empty attributes")
            }

            val delta = (find(rootObject, "ops")?.toString() ?: "")
                // замена относительных путей файлов на абсолютные
                .replace(Regex("(/uploads.+?)\"")) { result: MatchResult ->
                    NewsPostRepository.BASE_URL + result.value
                }

            return NewsPost(
                id,
                date,
                title,
                logo,
                text,
                delta
            )
        }

        /**
         * Рекурсивный поиск элемента.
         * @param root начала поиска.
         * @param key необходимый элемент.
         * @param treeKey элемент, по котору дальше продолжает поиск.
         * @return JSON объект нужного элемента. Возвращает null если не найден.
         */
        private fun find(root: JsonObject, key: String, treeKey: String = "delta"): JsonElement? {
            if (root.has(key)) {
                return root.get(key)
            }
            if (root.has(treeKey)) {
                return find(root.get(treeKey).asJsonObject, key, treeKey)
            }
            return null
        }
    }
}
