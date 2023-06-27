package com.vereshchagin.nikolay.stankinschedule.core.data.di

import com.vereshchagin.nikolay.stankinschedule.core.data.repository.DeviceRepositoryImpl
import com.vereshchagin.nikolay.stankinschedule.core.domain.repository.DeviceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface DeviceCoreModule {

    @Binds
    @ViewModelScoped
    fun provideDeviceRepository(repository: DeviceRepositoryImpl): DeviceRepository
}