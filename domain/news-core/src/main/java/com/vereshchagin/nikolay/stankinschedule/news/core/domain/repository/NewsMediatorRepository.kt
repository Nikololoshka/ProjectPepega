package com.vereshchagin.nikolay.stankinschedule.news.core.domain.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.model.NewsPost

interface NewsMediatorRepository {

    @OptIn(ExperimentalPagingApi::class)
    fun newsMediator(newsSubdivision: Int): RemoteMediator<Int, NewsPost>

}