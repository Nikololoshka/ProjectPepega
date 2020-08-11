package com.vereshchagin.nikolay.stankinschedule.repository

import android.content.Context

class NewsHomeRepository(context: Context) {

    companion object {
        val SUBDIVISIONS = listOf(0, 125)
    }

    private val repositories = ArrayList<NewsRepository>()

    init {
        for (id in SUBDIVISIONS) {
            repositories.add(NewsRepository(id, context))
        }
    }
}