package com.vereshchagin.nikolay.stankinschedule.news.di

import com.google.gson.GsonBuilder
import com.vereshchagin.nikolay.stankinschedule.news.data.api.PostResponse
import com.vereshchagin.nikolay.stankinschedule.news.data.api.StankinNewsAPI
import com.vereshchagin.nikolay.stankinschedule.news.data.repository.NewsRepositoryImpl
import com.vereshchagin.nikolay.stankinschedule.news.data.repository.PostRepositoryImpl
import com.vereshchagin.nikolay.stankinschedule.news.domain.repository.NewsRepository
import com.vereshchagin.nikolay.stankinschedule.news.domain.repository.PostRepository
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
    fun provideNewsRepository(repository: NewsRepositoryImpl): NewsRepository = repository

    @Provides
    @ViewModelScoped
    fun providePostRepository(repository: PostRepositoryImpl): PostRepository = repository
}