package com.vereshchagin.nikolay.stankinschedule.modulejournal.di

import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.modulejournal.data.api.ModuleJournalAPI
import com.vereshchagin.nikolay.stankinschedule.modulejournal.data.repository.*
import com.vereshchagin.nikolay.stankinschedule.modulejournal.domain.repository.*
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
object ModuleJournalModule {

    @Provides
    @ViewModelScoped
    fun provideModuleJournalService(client: OkHttpClient): ModuleJournalAPI {
        return Retrofit.Builder()
            .baseUrl(ModuleJournalAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .client(client)
            .build()
            .create(ModuleJournalAPI::class.java)
    }

    @Provides
    @ViewModelScoped
    fun provideServiceRepository(
        repository: JournalServiceRepositoryImpl,
    ): JournalServiceRepository = repository

    @Provides
    @ViewModelScoped
    fun provideStorageRepository(
        repository: JournalStorageRepositoryImpl,
    ): JournalStorageRepository = repository

    @Provides
    @ViewModelScoped
    fun provideSecureRepository(
        repository: JournalSecureRepositoryImpl,
    ): JournalSecureRepository = repository

    @Provides
    @ViewModelScoped
    fun providePredictRepository(
        repository: JournalPredictRepositoryImpl,
    ): JournalPredictRepository = repository

    @Provides
    @ViewModelScoped
    fun provideJournalRepository(
        repository: JournalRepositoryImpl,
    ): JournalRepository = repository
}