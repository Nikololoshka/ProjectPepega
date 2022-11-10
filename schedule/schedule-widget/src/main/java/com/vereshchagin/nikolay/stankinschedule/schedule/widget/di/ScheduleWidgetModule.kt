package com.vereshchagin.nikolay.stankinschedule.schedule.widget.di

import com.vereshchagin.nikolay.stankinschedule.schedule.widget.data.repository.ScheduleWidgetPreferenceImpl
import com.vereshchagin.nikolay.stankinschedule.schedule.widget.domain.repository.ScheduleWidgetPreference
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
interface ScheduleWidgetModule {

    @Binds
    fun provideScheduleWidgetPreference(pref: ScheduleWidgetPreferenceImpl): ScheduleWidgetPreference
}