package com.vereshchagin.nikolay.stankinschedule.repository

import android.content.Context
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.vereshchagin.nikolay.stankinschedule.db.MainApplicationDatabase

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

    fun latest(count: Int = 3) = LivePagedListBuilder(
        db.news().latest(count),
        PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(count)
            .setPageSize(1)
            .build()
    ).build()


    companion object {
        val SUBDIVISIONS = listOf(0, 125)
    }
}