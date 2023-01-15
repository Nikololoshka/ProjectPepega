package com.vereshchagin.nikolay.stankinschedule.news.core.data.di

import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.news.core.data.api.PostResponse
import com.vereshchagin.nikolay.stankinschedule.news.core.data.api.StankinNewsAPI
import com.vereshchagin.nikolay.stankinschedule.news.core.data.repository.*
import com.vereshchagin.nikolay.stankinschedule.news.core.domain.repository.*
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
object NewsModule {

    @Provides
    @ViewModelScoped
    fun provideNewsService(client: OkHttpClient): StankinNewsAPI {
        return Retrofit.Builder()
            .baseUrl(StankinNewsAPI.BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .registerTypeAdapter(
                            PostResponse.NewsPost::class.java, PostResponse.NewsPostDeserializer()
                        )
                        .create()
                )
            )
            .client(client)
            .build()
            .create(StankinNewsAPI::class.java)
    }

    @Provides
    @ViewModelScoped
    fun provideNewsStorageRepository(
        repository: NewsStorageRepositoryImpl
    ): NewsStorageRepository = repository

    @Provides
    @ViewModelScoped
    fun provideNewsRemoteRepository(
        repository: NewsRemoteRepositoryImpl
    ): NewsRemoteRepository = repository

    @Provides
    @ViewModelScoped
    fun provideNewsMediatorRepository(
        repository: NewsMediatorRepositoryImpl
    ): NewsMediatorRepository = repository

    @Provides
    @ViewModelScoped
    fun provideNewsPreferenceRepository(
        repository: NewsPreferenceRepositoryImpl
    ): NewsPreferenceRepository = repository

    @Provides
    @ViewModelScoped
    fun provideNewsPostRepository(
        repository: NewsPostRepositoryImpl
    ): NewsPostRepository = repository
}