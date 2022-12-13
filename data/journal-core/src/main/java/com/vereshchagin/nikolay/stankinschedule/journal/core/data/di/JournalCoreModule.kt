package com.vereshchagin.nikolay.stankinschedule.journal.core.data.di

import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.journal.core.data.api.ModuleJournalAPI
import com.vereshchagin.nikolay.stankinschedule.journal.core.data.repository.*
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.*
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.utils.Constants
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
object JournalCoreModule {

    @Provides
    @ViewModelScoped
    fun provideModuleJournalService(client: OkHttpClient): ModuleJournalAPI {
        return Retrofit.Builder()
            .baseUrl(Constants.MODULE_JOURNAL_URL)
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
    fun provideJournalRepository(
        repository: JournalRepositoryImpl,
    ): JournalRepository = repository

    @Provides
    @ViewModelScoped
    fun providePagingRepository(
        repository: JournalPagingRepositoryImpl,
    ): JournalPagingRepository = repository
}