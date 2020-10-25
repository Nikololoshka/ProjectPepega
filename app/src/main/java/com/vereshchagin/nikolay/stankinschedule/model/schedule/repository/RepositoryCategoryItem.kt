package com.vereshchagin.nikolay.stankinschedule.model.schedule.repository

import com.google.gson.annotations.SerializedName
import org.joda.time.DateTime

/**
 * Элемент в категории репозитория с расписаниями.
 */
data class RepositoryCategoryItem(
    @SerializedName("categoryName")
    val categoryName: String,
    @SerializedName("schedules")
    val schedules: List<String>,
    @SerializedName("date")
    val date: DateTime = DateTime.now()
)