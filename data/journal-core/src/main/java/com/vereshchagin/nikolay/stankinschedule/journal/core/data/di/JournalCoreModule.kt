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
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ViewModelComponent::class, SingletonComponent::class)
object JournalCoreModule {

    @Provides
    // Unscoped
    fun provideModuleJournalService(client: OkHttpClient): ModuleJournalAPI {
        return Retrofit.Builder()
            .baseUrl(Constants.MODULE_JOURNAL_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .client(client)
            .build()
            .create(ModuleJournalAPI::class.java)
    }

    @Provides
    // Unscoped
    fun provideServiceRepository(
        repository: JournalServiceRepositoryImpl,
    ): JournalServiceRepository = repository

    @Provides
    // Unscoped
    fun provideStorageRepository(
        repository: JournalStorageRepositoryImpl,
    ): JournalStorageRepository = repository

    @Provides
    // Unscoped
    fun provideSecureRepository(
        repository: JournalSecureRepositoryImpl,
    ): JournalSecureRepository = repository

    @Provides
    // Unscoped
    fun provideJournalRepository(
        repository: JournalRepositoryImpl,
    ): JournalRepository = repository

    @Provides
    // Unscoped
    fun providePagingRepository(
        repository: JournalPagingRepositoryImpl,
    ): JournalPagingRepository = repository

    @Provides
    fun provideJournalPreference(
        preference: JournalPreferenceImpl
    ): JournalPreference = preference
}