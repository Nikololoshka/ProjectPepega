package com.vereshchagin.nikolay.stankinschedule.news.core.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.vereshchagin.nikolay.stankinschedule.news.core.data.source.NewsRemoteSource
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsPost
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.repository.NewsMediatorRepository
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.repository.NewsRemoteRepository
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.repository.NewsStorageRepository
import javax.inject.Inject

class NewsMediatorRepositoryImpl @Inject constructor(
    private val remoteRepository: NewsRemoteRepository,
    private val storageRepository: NewsStorageRepository
) : NewsMediatorRepository {

    @ExperimentalPagingApi
    override fun newsMediator(newsSubdivision: Int): RemoteMediator<Int, NewsPost> {
        return NewsRemoteSource(
            newsSubdivision = newsSubdivision,
            remoteRepository = remoteRepository,
            storageRepository = storageRepository
        )
    }
}