package com.vereshchagin.nikolay.stankinschedule.schedule.core.di

import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.repository.ScheduleStorageImpl
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ScheduleCoreModule {

    @Binds
    @Singleton
    fun provideScheduleStorage(storage: ScheduleStorageImpl): ScheduleStorage

}