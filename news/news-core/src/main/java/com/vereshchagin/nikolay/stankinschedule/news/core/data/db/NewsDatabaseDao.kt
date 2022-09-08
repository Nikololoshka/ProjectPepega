package com.vereshchagin.nikolay.stankinschedule.news.core.data.db

interface NewsDatabaseDao {

    fun news(): NewsDao
}