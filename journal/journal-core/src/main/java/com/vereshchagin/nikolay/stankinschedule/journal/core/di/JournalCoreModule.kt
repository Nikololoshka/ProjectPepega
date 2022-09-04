package com.vereshchagin.nikolay.stankinschedule.journal.core.di

import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.journal.core.data.api.ModuleJournalAPI
import com.vereshchagin.nikolay.stankinschedule.journal.core.data.repository.JournalRepositoryImpl
import com.vereshchagin.nikolay.stankinschedule.journal.core.data.repository.JournalSecureRepositoryImpl
import com.vereshchagin.nikolay.stankinschedule.journal.core.data.repository.JournalServiceRepositoryImpl
import com.vereshchagin.nikolay.stankinschedule.journal.core.data.repository.JournalStorageRepositoryImpl
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalRepository
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalSecureRepository
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalServiceRepository
import com.vereshchagin.nikolay.stankinschedule.journal.core.domain.repository.JournalStorageRepository
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
    fun provideJournalRepository(
        repository: JournalRepositoryImpl,
    ): JournalRepository = repository
}