package com.vereshchagin.nikolay.stankinschedule.schedule.di

import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.schedule.data.api.ScheduleRepositoryAPI
import com.vereshchagin.nikolay.stankinschedule.schedule.data.repository.FirebaseRemoteService
import com.vereshchagin.nikolay.stankinschedule.schedule.data.repository.RepositoryStorageImpl
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.repository.RepositoryStorage
import com.vereshchagin.nikolay.stankinschedule.schedule.domain.repository.ScheduleRemoteService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ViewModelComponent::class)
object ScheduleRemoteModule {

    @Provides
    @ViewModelScoped
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
    @ViewModelScoped
    fun provideRepositoryService(service: FirebaseRemoteService): ScheduleRemoteService = service


    @Provides
    @ViewModelScoped
    fun provideRepositoryStorage(storage: RepositoryStorageImpl): RepositoryStorage = storage

}