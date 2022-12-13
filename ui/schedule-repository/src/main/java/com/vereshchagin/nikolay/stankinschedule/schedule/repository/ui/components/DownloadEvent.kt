package com.vereshchagin.nikolay.stankinschedule.schedule.repository.ui.components

import com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.model.RepositoryItem

sealed interface DownloadEvent {
    class StartDownload(val scheduleName: String, val item: RepositoryItem) : DownloadEvent
}