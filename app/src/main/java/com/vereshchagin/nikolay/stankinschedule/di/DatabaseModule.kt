package com.vereshchagin.nikolay.stankinschedule.di

import android.content.Context
import androidx.room.RoomDatabase
import com.vereshchagin.nikolay.stankinschedule.db.MainApplicationDatabase
import com.vereshchagin.nikolay.stankinschedule.db.dao.NewsDao
import com.vereshchagin.nikolay.stankinschedule.db.dao.RepositoryDao
import com.vereshchagin.nikolay.stankinschedule.db.dao.ScheduleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideMainApplicationDatabase(
        @ApplicationContext context: Context,
    ): MainApplicationDatabase {
        return MainApplicationDatabase.database(context)
    }

    @Singleton
    @Provides
    fun provideRoomDatabase(db: MainApplicationDatabase): RoomDatabase = db

    @Singleton
    @Provides
    fun provideNewssDao(db: MainApplicationDatabase): com.vereshchagin.nikolay.stankinschedule.news.data.db.NewsDao =
        db.featureNews()

    @Singleton
    @Provides
    fun provideSchedulesDao(db: MainApplicationDatabase): ScheduleDao = db.schedules()

    @Singleton
    @Provides
    fun provideNewsDao(db: MainApplicationDatabase): NewsDao = db.news()

    @Singleton
    @Provides
    fun provideScheduleRemoteDao(db: MainApplicationDatabase): RepositoryDao = db.repository()
}