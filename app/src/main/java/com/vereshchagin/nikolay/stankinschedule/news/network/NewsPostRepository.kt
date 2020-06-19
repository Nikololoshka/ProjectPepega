package com.vereshchagin.nikolay.stankinschedule.news.network

import com.vereshchagin.nikolay.stankinschedule.news.model.NewsPost
import com.vereshchagin.nikolay.stankinschedule.news.post.paging.Listing


interface NewsPostRepository {

    fun posts(page: Int, size: Int) : Listing<NewsPost>
}