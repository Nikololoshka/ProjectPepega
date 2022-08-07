package com.vereshchagin.nikolay.stankinschedule.schedule.ui.repository.components

import com.vereshchagin.nikolay.stankinschedule.schedule.domain.model.RepositoryItem

sealed interface DownloadState {
    class StartDownload(val scheduleName: String, val item: RepositoryItem) : DownloadState
    class RequiredName(val scheduleName: String, val item: RepositoryItem) : DownloadState
}