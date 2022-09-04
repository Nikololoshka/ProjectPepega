package com.vereshchagin.nikolay.stankinschedule.journal.predict.di

import com.vereshchagin.nikolay.stankinschedule.journal.predict.data.repository.JournalPredictRepositoryImpl
import com.vereshchagin.nikolay.stankinschedule.journal.predict.domain.repository.JournalPredictRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface JournalPredictModule {

    @Binds
    @ViewModelScoped
    fun providePredictRepository(
        repository: JournalPredictRepositoryImpl,
    ): JournalPredictRepository
}