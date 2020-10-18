package com.vereshchagin.nikolay.stankinschedule.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.vereshchagin.nikolay.stankinschedule.db.MainApplicationDatabase
import com.vereshchagin.nikolay.stankinschedule.model.news.NewsItem

class NewsHomeRepository(context: Context) {

    private val repositories = ArrayList<NewsRepository>()
    private var db = MainApplicationDatabase.database(context)

    init {
        for (id in SUBDIVISIONS) {
            repositories.add(NewsRepository(id, context))
        }
    }

    fun updateAll() {
        repositories.forEach { it.refresh() }
    }

    fun latest(count: Int = 3): LiveData<PagingData<NewsItem>> {
        return Pager(PagingConfig(count)) {
            db.news().latest(count)
        }.liveData
    }


    companion object {
        val SUBDIVISIONS = listOf(0, 125)
    }
}