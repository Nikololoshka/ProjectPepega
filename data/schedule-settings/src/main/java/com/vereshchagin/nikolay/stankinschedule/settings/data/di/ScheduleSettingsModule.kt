package com.vereshchagin.nikolay.stankinschedule.settings.data.di

import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.repository.SchedulePreference
import com.vereshchagin.nikolay.stankinschedule.settings.data.repository.ScheduleDataStore
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