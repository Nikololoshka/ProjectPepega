package com.vereshchagin.nikolay.stankinschedule.ical.data.di

import com.vereshchagin.nikolay.stankinschedule.ical.data.repository.ICalRepository
import com.vereshchagin.nikolay.stankinschedule.schedule.ical.domain.repository.ICalExporter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface ICalModule {

    @Binds
    @ViewModelScoped
    fun provideICalRepository(repository: ICalRepository): ICalExporter

}