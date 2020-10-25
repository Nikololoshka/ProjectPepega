package com.vereshchagin.nikolay.stankinschedule.model.news

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.vereshchagin.nikolay.stankinschedule.repository.NewsRepository

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
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("logo")
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

    /**
     * Возвращает только дату из публикации.
     */
    fun onlyDate() = date.split(" ").first()
}