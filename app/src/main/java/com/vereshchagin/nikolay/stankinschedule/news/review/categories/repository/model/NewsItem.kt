package com.vereshchagin.nikolay.stankinschedule.news.review.categories.repository.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.vereshchagin.nikolay.stankinschedule.news.review.categories.repository.NewsRepository

/**
 * Данные о новости.
 * @param id номер новости.
 * @param title заголовок.
 * @param date дата публикации новости.
 * @param logo относительный путь к картинке новости.
 * @param shortText короткий текст новости.
 * @param author номер автора новости.
 */
@Entity(tableName = "posts")
data class NewsItem(
    @PrimaryKey
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
     * Индекс (номер) новости по порядку.
     */
    var indexInResponse: Int = -1

    /**
     * Номер отдела, кому принадлежит новость.
     */
    var newsSubdivision: Int = -1

    /**
     * Возвращает url к картинке новости.
     */
    fun logoUrl() = NewsRepository.BASE_URL + logo
}