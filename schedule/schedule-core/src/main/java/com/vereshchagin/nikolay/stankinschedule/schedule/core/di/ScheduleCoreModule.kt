package com.vereshchagin.nikolay.stankinschedule.schedule.core.di

import com.vereshchagin.nikolay.stankinschedule.schedule.core.data.repository.ScheduleStorageImpl
import com.vereshchagin.nikolay.stankinschedule.schedule.core.domain.repository.ScheduleStorage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface ScheduleCoreModule {

    @Binds
    @ViewModelScoped
    fun provideScheduleStorage(storage: ScheduleStorageImpl): ScheduleStorage

}