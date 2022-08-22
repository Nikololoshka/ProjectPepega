package com.vereshchagin.nikolay.stankinschedule.schedule.repository.di

import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.repository.FirebaseRemoteService
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.data.repository.RepositoryStorageImpl
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.repository.RepositoryStorage
import com.vereshchagin.nikolay.stankinschedule.schedule.repository.domain.repository.ScheduleRemoteService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface RepositoryModule {

    @Binds
    @ViewModelScoped
    fun provideRepositoryService(service: FirebaseRemoteService): ScheduleRemoteService

    @Binds
    @ViewModelScoped
    fun provideRepositoryStorage(storage: RepositoryStorageImpl): RepositoryStorage

}