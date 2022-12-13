package com.vereshchagin.nikolay.stankinschedule.core.data.di

import com.vereshchagin.nikolay.stankinschedule.core.data.preference.PreferenceManager
import com.vereshchagin.nikolay.stankinschedule.core.domain.settings.PreferenceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun providePreferenceManager(manager: PreferenceManager): PreferenceRepository = manager

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }
}