package com.vereshchagin.nikolay.stankinschedule.di

import com.vereshchagin.nikolay.stankinschedule.core.domain.logger.LoggerAnalytics
import com.vereshchagin.nikolay.stankinschedule.logger.FirebaseLoggerAnalytics
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    @Binds
    @Singleton
    fun provideAnalytics(analytics: FirebaseLoggerAnalytics): LoggerAnalytics

}