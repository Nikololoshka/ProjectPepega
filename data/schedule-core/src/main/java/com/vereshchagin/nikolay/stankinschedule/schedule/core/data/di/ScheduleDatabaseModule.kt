package com.vereshchagin.nikolay.stankinschedule.schedule.core.data.di

import android.content.Context
import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.db.ScheduleDao
import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.db.ScheduleDatabase
import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.repository.ScheduleStorageImpl
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScheduleDatabaseModule {

    @Singleton
    @Provides
    fun provideScheduleDatabase(
        @ApplicationContext context: Context,
    ): ScheduleDatabase = ScheduleDatabase.database(context)

    @Singleton
    @Provides
    fun provideScheduleDao(db: ScheduleDatabase): ScheduleDao = db.schedule()

    @Singleton
    @Provides
    fun provideScheduleStorage(storage: ScheduleStorageImpl): ScheduleStorage = storage
}