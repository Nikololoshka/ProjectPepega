package com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.di

import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.api.ScheduleRepositoryAPI
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.repository.FirebaseLoaderService
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.repository.ScheduleLoaderService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryNetworkModule {

    @Provides
    @Singleton
    fun provideFirebaseRemoteService(client: OkHttpClient): ScheduleRepositoryAPI {
        return Retrofit.Builder()
            .baseUrl(ScheduleRepositoryAPI.FIREBASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .create()
                )
            )
            .client(client)
            .build()
            .create(ScheduleRepositoryAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideLoaderService(service: FirebaseLoaderService): ScheduleLoaderService = service
}