package com.vereshchagin.nikolay.stankinschedule.news.core.data.di

import android.content.Context
import com.vereshchagin.nikolay.stankinschedule.news.core.data.db.NewsDao
import com.vereshchagin.nikolay.stankinschedule.news.core.data.db.NewsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NewsDatabaseModule {

    @Singleton
    @Provides
    fun provideRepositoryDatabase(
        @ApplicationContext context: Context,
    ): NewsDatabase = NewsDatabase.database(context)

    @Singleton
    @Provides
    fun provideRepositoryDao(db: NewsDatabase): NewsDao = db.news()

}