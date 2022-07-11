package com.vereshchagin.nikolay.stankinschedule.news.data.db

interface NewsDatabaseDao {

    fun featureNews(): NewsDao
}