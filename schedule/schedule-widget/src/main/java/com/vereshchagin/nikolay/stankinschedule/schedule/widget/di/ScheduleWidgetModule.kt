package com.vereshchagin.nikolay.stankinschedule.schedule.widget.di

import com.vereshchagin.nikolay.stankinschedule.schedule.widget.data.repository.ScheduleWidgetPreferenceImpl
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.domain.repository.ScheduleWidgetPreference
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class, ServiceComponent::class)
interface ScheduleWidgetModule {

    @Binds
    // Unscoped
    fun providePreference(pref: ScheduleWidgetPreferenceImpl): ScheduleWidgetPreference
}