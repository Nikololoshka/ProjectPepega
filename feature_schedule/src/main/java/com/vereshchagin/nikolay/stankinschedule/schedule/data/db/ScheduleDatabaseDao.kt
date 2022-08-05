package com.vereshchagin.nikolay.stankinschedule.schedule.data.db

interface ScheduleDatabaseDao {

    fun schedule(): ScheduleDao

    fun repository(): RepositoryDao

}