package com.vereshchagin.nikolay.stankinschedule.parser.data.di

import com.vereshchagin.nikolay.stankinschedule.parser.data.repository.PDFRepositoryImpl
import com.vereshchagin.nikolay.stankinschedule.schedule.parser.domain.repository.PDFRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface ParserModule {

    @Binds
    @ViewModelScoped
    fun provideParseRepository(repository: PDFRepositoryImpl): PDFRepository

}