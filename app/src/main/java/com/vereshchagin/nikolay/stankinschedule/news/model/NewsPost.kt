package com.vereshchagin.nikolay.stankinschedule.news.model

import com.google.gson.annotations.SerializedName
import com.vereshchagin.nikolay.stankinschedule.news.network.StankinNewsService

/**
 * Данные о новости.
 * @param id номер новости.
 * @param title заголовок.
 * @param date дата публикации новости.
 * @param logo относительный путь к картинке новости.
 * @param shortText короткий текст новости.
 * @param author номер автора новости.
 */
class NewsPost(
    val id: Int,
    val title: String,
    val date: String,
    val logo: String,
    @SerializedName("short_text")
    val shortText: String,
    @SerializedName("author_id")
    val author: Int
) {
    /**
     * Возвращает url к картинке новости.
     */
    fun logoUrl() = StankinNewsService.BASE_URL + logo

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NewsPost

        if (id != other.id) return false
        if (title != other.title) return false
        if (date != other.date) return false
        if (logo != other.logo) return false
        if (shortText != other.shortText) return false
        if (author != other.author) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + logo.hashCode()
        result = 31 * result + shortText.hashCode()
        result = 31 * result + author
        return result
    }
}