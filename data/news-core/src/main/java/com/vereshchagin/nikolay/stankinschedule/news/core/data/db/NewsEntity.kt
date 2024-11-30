package com.vereshchagin.nikolay.stankinschedule.news.core.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Данные о новости.
 */
@Entity(
    tableName = "news_posts",
    indices = [
        Index("relative_url", unique = true)
    ]
)
data class NewsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "index_order") val indexOrder: Int,
    @ColumnInfo(name = "news_subdivision") val newsSubdivision: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "logo") val logo: String,
    @ColumnInfo(name = "relative_url") val relativeUrl: String?,
)