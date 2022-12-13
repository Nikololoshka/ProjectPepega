package com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.model

data class RepositoryDescription(
    val lastUpdate: String,
    val categories: List<RepositoryCategory>,
)