package com.vereshchagin.nikolay.stankinschedule.news.review.di

import com.vereshchagin.nikolay.stankinschedule.news.review.data.repository.NewsRepositoryImpl
import com.vereshchagin.nikolay.stankinschedule.news.review.domain.repository.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface NewsReviewModule {

    @Binds
    @ViewModelScoped
    fun provideNewsRepository(repository: NewsRepositoryImpl): NewsRepository

}