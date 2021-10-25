package com.vereshchagin.nikolay.stankinschedule.di

import android.content.Context
import com.vereshchagin.nikolay.stankinschedule.settings.ApplicationPreferenceKt
import com.vereshchagin.nikolay.stankinschedule.settings.SchedulePreferenceKt
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object PreferenceModule {

    @Provides
    fun provideApplicationPreference(
        @ApplicationContext context: Context,
    ): ApplicationPreferenceKt {
        return ApplicationPreferenceKt(context)
    }

    @Provides
    fun provideSchedulePreference(
        @ApplicationContext context: Context,
    ): SchedulePreferenceKt {
        return SchedulePreferenceKt(context)
    }
}