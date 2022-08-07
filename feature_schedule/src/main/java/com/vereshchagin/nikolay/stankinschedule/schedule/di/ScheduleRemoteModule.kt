package com.vereshchagin.nikolay.stankinschedule.schedule.di

import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.schedule.data.api.ScheduleRepositoryAPI
import com.vereshchagin.nikolay.stankinschedule.schedule.data.repository.FirebaseRemoteService
import com.vereshchagin.nikolay.stankinschedule.schedule.data.repository.RepositoryStorageImpl
import com.vereshchagin.nikolay.stankinschedule.schedule.data.repository.ScheduleStorageImpl
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.repository.RepositoryStorage
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.repository.ScheduleRemoteService
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.repository.ScheduleStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
interface ScheduleRemoteModule {

    @Binds
    @ViewModelScoped
    fun provideRepositoryService(service: FirebaseRemoteService): ScheduleRemoteService

    @Binds
    @ViewModelScoped
    fun provideRepositoryStorage(storage: RepositoryStorageImpl): RepositoryStorage

    @Binds
    @ViewModelScoped
    fun provideScheduleStorage(storage: ScheduleStorageImpl): ScheduleStorage
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

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
}