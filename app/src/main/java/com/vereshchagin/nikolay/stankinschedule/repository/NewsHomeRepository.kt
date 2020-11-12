package com.vereshchagin.nikolay.stankinschedule.repository

import android.content.Context
import com.vereshchagin.nikolay.stankinschedule.db.MainApplicationDatabase

/**
 * Репозиторий для последних новостей на главной странице.
 */
class NewsHomeRepository(context: Context) {

    /**
     * Список репозиториев.
     */
    private val repositories = SUBDIVISIONS.map {
        NewsRepository(it, context)
    }

    /**
     * БД с новостями.
     */
    private var db = MainApplicationDatabase.database(context)


    /**
     * Обновить все репозитории с новостями.
     */
    suspend fun updateAll() {
        repositories.forEach { it.refresh() }
    }

    /**
     * LiveData на список с последними новостями.
     */
    fun latest(count: Int = 3) = db.news().latest(count)

    companion object {
        /**
         * Подразделения новостей (для репозиториев).
         */
        val SUBDIVISIONS = listOf(0, 125)
    }
}