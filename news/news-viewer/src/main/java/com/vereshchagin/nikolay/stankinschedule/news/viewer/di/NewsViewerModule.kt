package com.vereshchagin.nikolay.stankinschedule.news.viewer.di

import com.vereshchagin.nikolay.stankinschedule.news.viewer.data.repository.PostRepositoryImpl
import com.vereshchagin.nikolay.stankinschedule.news.viewer.domain.repository.PostRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface NewsViewerModule {

    @Binds
    @ViewModelScoped
    fun providePostRepository(repository: PostRepositoryImpl): PostRepository

}