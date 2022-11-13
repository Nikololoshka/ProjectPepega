package com.vereshchagin.nikolay.stankinschedule.schedule.settings.di

import com.vereshchagin.nikolay.stankinschedule.schedule.settings.data.repository.ScheduleDataStore
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.repository.SchedulePreference
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ScheduleSettingsModule {

    @Binds
    @Singleton
    fun provideSchedulePreference(preference: ScheduleDataStore): SchedulePreference
}