package com.vereshchagin.nikolay.stankinschedule.di

import android.content.Context
import com.vereshchagin.nikolay.stankinschedule.db.MainApplicationDatabase
import com.vereshchagin.nikolay.stankinschedule.db.dao.NewsDao
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
        @ApplicationContext context: Context
    ): MainApplicationDatabase {
        return MainApplicationDatabase.database(context)
    }

    @Singleton
    @Provides
    fun provideNewsDao(db: MainApplicationDatabase): NewsDao = db.news()
}