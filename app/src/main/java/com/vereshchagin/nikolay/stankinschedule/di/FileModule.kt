package com.vereshchagin.nikolay.stankinschedule.di

import android.content.Context
import com.vereshchagin.nikolay.stankinschedule.utils.CacheFolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FileModule {

    @Provides
    fun provideCacheFolder(@ApplicationContext context: Context): CacheFolder {
        return CacheFolder(context.cacheDir)
    }
}