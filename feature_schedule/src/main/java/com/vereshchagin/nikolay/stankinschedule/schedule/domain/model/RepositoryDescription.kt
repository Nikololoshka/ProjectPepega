package com.vereshchagin.nikolay.stankinschedule.schedule.domain.model

data class RepositoryDescription(
    val lastUpdate: String,
    val categories: List<RepositoryCategory>,
)