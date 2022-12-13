package com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.di

import android.content.Context
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.db.RepositoryDao
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.db.RepositoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryDatabaseModule {

    @Singleton
    @Provides
    fun provideRepositoryDatabase(
        @ApplicationContext context: Context,
    ): RepositoryDatabase = RepositoryDatabase.database(context)

    @Singleton
    @Provides
    fun provideRepositoryDao(db: RepositoryDatabase): RepositoryDao = db.repository()

}