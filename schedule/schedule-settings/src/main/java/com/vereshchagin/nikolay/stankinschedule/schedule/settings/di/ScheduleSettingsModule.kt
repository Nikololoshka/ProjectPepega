package com.vereshchagin.nikolay.stankinschedule.schedule.settings.di

import com.vereshchagin.nikolay.stankinschedule.schedule.settings.data.repository.ScheduleDataStore
import com.vereshchagin.nikolay.stankinschedule.schedule.settings.domain.repository.SchedulePreference
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface ScheduleSettingsModule {

    @Binds
    @ViewModelScoped
    fun provideSchedulePreference(preference: ScheduleDataStore): SchedulePreference
}