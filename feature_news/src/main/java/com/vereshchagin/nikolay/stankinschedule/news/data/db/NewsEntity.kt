package com.vereshchagin.nikolay.stankinschedule.news.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Данные о новости.
 */
@Entity(tableName = "news_posts")
data class NewsEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "index_order") val indexOrder: Int,
    @ColumnInfo(name = "news_subdivision") val newsSubdivision: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "logo") val logo: String,
)