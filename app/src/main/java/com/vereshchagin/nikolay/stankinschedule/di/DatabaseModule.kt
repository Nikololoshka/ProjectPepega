package com.vereshchagin.nikolay.stankinschedule.di

import android.content.Context
import androidx.room.RoomDatabase
import com.vereshchagin.nikolay.stankinschedule.db.MainApplicationDatabase
import com.vereshchagin.nikolay.stankinschedule.news.core.data.db.NewsDao
import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.db.ScheduleDao
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.db.RepositoryDao
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
    ): MainApplicationDatabase = MainApplicationDatabase.database(context)

    @Singleton
    @Provides
    fun provideRoomDatabase(db: MainApplicationDatabase): RoomDatabase = db

    @Singleton
    @Provides
    fun provideNewsDao(db: MainApplicationDatabase): NewsDao = db.news()

    @Singleton
    @Provides
    fun provideScheduleDao(db: MainApplicationDatabase): ScheduleDao = db.schedule()

    @Singleton
    @Provides
    fun provideRepositoryDao(db: MainApplicationDatabase): RepositoryDao = db.repository()
}