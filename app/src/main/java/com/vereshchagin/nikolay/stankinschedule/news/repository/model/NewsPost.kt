package com.vereshchagin.nikolay.stankinschedule.news.repository.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.vereshchagin.nikolay.stankinschedule.news.repository.StankinNewsRepository

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
data class NewsPost(
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
     * Возвращает url к картинке новости.
     */
    fun logoUrl() = StankinNewsRepository.BASE_URL + logo
}