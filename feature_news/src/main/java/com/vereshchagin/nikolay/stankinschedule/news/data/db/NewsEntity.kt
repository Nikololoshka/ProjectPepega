package com.vereshchagin.nikolay.stankinschedule.news.data.db

import androidx.room.Entity

/**
 * Данные о новости.
 */
@Entity(tableName = "posts")
data class NewsEntity(
    val id: Int,
    val indexOrder: Int,
    val newsSubdivision: Int,
    val title: String,
    val date: String,
    val logo: String,
)