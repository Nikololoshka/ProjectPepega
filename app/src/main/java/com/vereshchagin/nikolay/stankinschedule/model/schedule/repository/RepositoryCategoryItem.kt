package com.vereshchagin.nikolay.stankinschedule.model.schedule.repository

import org.joda.time.DateTime

/**
 * Элемент в категории репозитория с расписаниями.
 */
data class RepositoryCategoryItem(
    val categoryName: String,
    val schedules: List<String>,
    val date: DateTime = DateTime.now()
)